package org.cstr24.hyphenengine.interop.source.studiomdl;

import org.cstr24.hyphenengine.assets.AResourceLoader;
import org.cstr24.hyphenengine.assets.AssetLoadTask;
import org.cstr24.hyphenengine.data.*;
import org.cstr24.hyphenengine.filesystem.HyFile;
import org.cstr24.hyphenengine.geometry.*;
import org.cstr24.hyphenengine.interop.source.SourceAssetTypes;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.*;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx.BodyPartHeader_t;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx.VTXFile;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx.VTXParser;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vvd.VVDFile;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vvd.VVDParser;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vvd.mstudiovertex_t;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialManager;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class StudioModelLoader extends AResourceLoader<HyModel> {

    public StudioModelLoader(){

    }

    public StudioModel load(HyFile in){
        //we load a .mdl file and everything comes after that
        String fullFileName = in.getFileName();
        String fileParentPath = in.getFilePath().getParent().toString().concat("/");
        String fileNameSansExtension = fullFileName.substring(0, fullFileName.lastIndexOf('.'));

        String vtxPath = fileParentPath + fileNameSansExtension + ".dx90.vtx";
        VTXFile vtxFile = null;
        HyFile vtxFileHandle = HyFile.get(vtxPath);

        String vvdPath = fileParentPath + fileNameSansExtension + ".vvd";
        VVDFile vvdFile = null;
        HyFile vvdFileHandle = HyFile.get(vvdPath);

        var modelResult = new MDLParser().parse(in);

        if (vtxFileHandle.exists()){
            vtxFile = new VTXParser(modelResult.header.version).parse(HyFile.get(vtxPath));
        }else{
            System.out.println("Error: vtx file " + vtxPath + " not found.");
            //TODO - are fallbacks ever necessary, such as to .sw.vtx or .dx80.vtx? do all models have a dx90.vtx?
        }

        if (vvdFileHandle.exists()){
            vvdFile = new VVDParser().parse(HyFile.get(vvdPath));
        }else{
            System.out.println("Error: vvd file " + vvdFile + " not found.");
        }

        //TODO move levelOfDetail to be either a parameter or builder construction style
        int levelOfDetail = 0;
        setRootLOD(modelResult.header, modelResult.bodyParts, levelOfDetail);

        int vertexCount = vvdFile.header.numLODVertexes[levelOfDetail];
        int fixupCount = vvdFile.header.numFixups;
        int vertexArrayOffset = 0;

        //buffers to load into graphics library
        FloatBuffer positions = BufferUtils.createFloatBuffer(vertexCount * 3); //x, y, z
        FloatBuffer normals = BufferUtils.createFloatBuffer(vertexCount * 3); //x, y, z
        FloatBuffer uv0s = BufferUtils.createFloatBuffer(vertexCount * 2); //s, t [u, v]
        FloatBuffer tangents = BufferUtils.createFloatBuffer(vertexCount * 4); //x, y, z, w
        FloatBuffer boneWeights = BufferUtils.createFloatBuffer(vertexCount * 3);
        IntBuffer boneIDs = BufferUtils.createIntBuffer(vertexCount * 4); //bone 1, bone 2, bone 3, bone count

        if (fixupCount == 0){
            for (int vtx = 0; vtx < vertexCount; vtx++){
                var vertex = vvdFile.vertices.get(vertexArrayOffset);
                var tangent = vvdFile.tangents.get(vertexArrayOffset);

                //some initial thoughts... how do I know I'm reading the correct information based on my chosen LOD?

                populateBuffers(
                    vertex, tangent, vertexArrayOffset, positions, normals, uv0s, tangents, boneWeights, boneIDs
                );

                vertexArrayOffset++;
            }
        }else{
            //System.out.println(modelResult.header.name + " - mesh has fixups.");
            for (int fup = 0; fup < vvdFile.header.numFixups; fup++){
                var fixup = vvdFile.fixups.get(fup);

                if (fixup.lod >= levelOfDetail){
                    for (int vtx = 0; vtx < fixup.numVertexes; vtx++){
                        int vertexIndex = fixup.sourceVertexID + vtx;

                        var vertex = vvdFile.vertices.get(vertexIndex);
                        var tangent = vvdFile.tangents.get(vertexIndex);

                        populateBuffers(
                            vertex, tangent, vertexArrayOffset, positions, normals, uv0s, tangents, boneWeights, boneIDs
                        );

                        vertexArrayOffset++;
                    }
                }
            }
        }

        int indexCount = calculateIndexCount(vtxFile.bodyParts, levelOfDetail);
        short[] modelIndices = new short[indexCount];
        buildIndexData(vtxFile.bodyParts, modelResult, levelOfDetail, modelIndices);

        //System.out.println("done - transfer to model");

        modelResult.modelName = fileNameSansExtension;
        modelResult.userData = modelResult;

        HyMesh meshResult = HyMesh.create();
        meshResult.setVertexLayout(VertexLayout.with(
            VertexElement.of(ComponentType.Float, 3), //Positions
            VertexElement.of(ComponentType.Float, 3), //Normals
            VertexElement.of(ComponentType.Float, 2), //UV0
            VertexElement.of(ComponentType.Float, 4), //Tangents
            VertexElement.of(ComponentType.Float, 3), //Bone Weights
            VertexElement.of(ComponentType.UnsignedInt, 4) //Bone indices (max 3) and count
        ));

        GL46ImmutableBuffer elementBuffer = new GL46ImmutableBuffer();
        elementBuffer.setData(modelIndices);

        GL46ImmutableBuffer vertexData = new GL46ImmutableBuffer().allocate((long) vertexCount * meshResult.vertexLayout.sizeOf());
        ByteBuffer vertexDataMap = vertexData.map(MapMode.WriteOnly);

        //I want to do some manual interleaving for now
        for (int vtx = 0; vtx < vertexCount; vtx++){
            vertexDataMap.position(meshResult.vertexLayout.sizeOf() * vtx);
            int positionIndex = vtx * 3;
            int normalIndex = vtx * 3;
            int uv0Index = vtx * 2;
            int tangentIndex = vtx * 4;
            int boneWeightIndex = vtx * 3;
            int boneIDIndex = vtx * 4;

            vertexDataMap.putFloat(positions.get(positionIndex))
                .putFloat(positions.get(positionIndex + 1))
                .putFloat(positions.get(positionIndex + 2));

            vertexDataMap.putFloat(normals.get(normalIndex))
                .putFloat(normals.get(normalIndex + 1))
                .putFloat(normals.get(normalIndex + 2));

            vertexDataMap.putFloat(uv0s.get(uv0Index)).putFloat(uv0s.get(uv0Index + 1));

            vertexDataMap.putFloat(tangents.get(tangentIndex))
                    .putFloat(tangents.get(tangentIndex + 1))
                    .putFloat(tangents.get(tangentIndex + 2))
                    .putFloat(tangents.get(tangentIndex + 3));

            vertexDataMap.putFloat(boneWeights.get(boneWeightIndex))
                    .putFloat(boneWeights.get(boneWeightIndex + 1))
                    .putFloat(boneWeights.get(boneWeightIndex + 2));

            vertexDataMap.putInt(boneIDs.get(boneIDIndex))
                    .putInt(boneIDs.get(boneIDIndex + 1))
                    .putInt(boneIDs.get(boneIDIndex + 2))
                    .putInt(boneIDs.get(boneIDIndex + 3));
        }
        vertexData.unmap();

        int elemBaseOffset = 0;

        for (int bpIndex = 0; bpIndex < vtxFile.bodyParts.size(); bpIndex++){
            var bodyPart = vtxFile.bodyParts.get(bpIndex);
            var mdlBodyPart = modelResult.bodyParts.get(bpIndex);

            for (int modelID = 0; modelID < bodyPart.numModels; modelID++){
                var model = bodyPart.models.get(modelID);
                var mdlModel = mdlBodyPart.models.get(modelID);

                var lod = model.modelLODs.get(levelOfDetail);

                for (int meshID = 0; meshID < lod.numMeshes; meshID++){
                    var mesh = lod.meshHeaders.get(meshID);
                    MaterialInstance matInstance;
                    matInstance = MaterialManager.get().getMaterial("VertexLitGeneric").createInstance();

                    for (int stripGroupID = 0; stripGroupID < mesh.numStripGroups; stripGroupID++){
                        var stripGroup = mesh.stripGroupHeaders.get(stripGroupID);

                        for (int stripID = 0; stripID < stripGroup.numStrips; stripID++){
                            var strip = stripGroup.stripHeaders.get(stripID);

                            SubMesh subMesh = SubMesh.create(
                                    mdlModel.name,
                                    stripGroup.indexOffset + strip.indexOffset,
                                    strip.numIndices
                            ).elementBase(elemBaseOffset);

                            subMesh.materialInstance = matInstance;

                            elemBaseOffset += strip.numVerts;
                            subMesh.baseVertex = false;
                            subMesh.enable();

                            meshResult.addSubmesh(subMesh);
                        }
                    }
                }
            }
        }

        meshResult.setVertexData(vertexData, vertexCount);
        meshResult.setElementBuffer(elementBuffer, ComponentType.UnsignedShort, indexCount);
        meshResult.apply();

        modelResult.meshes.add(meshResult);

        modelResult.vvd = vvdFile;
        modelResult.vtx = vtxFile;

        return modelResult;
    }

    public int calculateIndexCount(ArrayList<BodyPartHeader_t> bodyParts, int lod){
        int[] numIndices = {0};

        bodyParts.forEach((bodyPart) -> {
            bodyPart.models.forEach((model) -> model.modelLODs.get(lod).meshHeaders.forEach((mesh) -> {
                mesh.stripGroupHeaders.forEach((stripGroup) -> {
                    numIndices[0] += stripGroup.numIndicies;
                });
            }));
        });

        return numIndices[0];
    }

    public void buildIndexData(ArrayList<BodyPartHeader_t> bodyParts, StudioModel modelFile, int lod, short[] indexArray){
        int vertexTableIndex;
        int indexOffset = 0;

        for (int bPart = 0; bPart < bodyParts.size(); bPart++){
            var bodyPart = bodyParts.get(bPart);
            var mdlBodyPart = modelFile.bodyParts.get(bPart);

            //System.out.println("VVD model headers for body part " + mdlBodyPart.bodyPartName + ": " + bodyPart.models.size() + " | mdl model headers: " + mdlBodyPart.models.size());
            for (int modelID = 0; modelID < 1; modelID++){
                var model = bodyPart.models.get(modelID);
                var mdlModel = mdlBodyPart.models.get(modelID);

                var lodHeader = model.modelLODs.get(lod);

                //System.out.println("\tModel " + modelID + " @ LOD " + lod + " VVD mesh headers for body part: " + lodHeader.meshHeaders.size() + " | mdl mesh headers: " + mdlModel.meshes.size());
                for (int meshID = 0; meshID < lodHeader.numMeshes; meshID++){

                    var meshHeader = lodHeader.meshHeaders.get(meshID);
                    var mdlMesh = mdlModel.meshes.get(meshID);

                    for (int stripGroupID = 0; stripGroupID < meshHeader.numStripGroups; stripGroupID++){
                        var stripGroupHeader = meshHeader.stripGroupHeaders.get(stripGroupID);

                        var vertTable = stripGroupHeader.vertices;
                        stripGroupHeader.indexOffset = indexOffset;

                        for (int i = 0; i < stripGroupHeader.numIndicies; i++){
                            vertexTableIndex = stripGroupHeader.indices.get(i);
                            int index = (vertTable.get(vertexTableIndex).origMeshVertID + mdlModel.vertexindex + mdlMesh.vertexoffset);

                            indexArray[indexOffset] = (short) index;
                            indexOffset++;
                        }
                    }
                }
            }
        }
    }

    public int setRootLOD(studiohdr_t header, ArrayList<mstudiobodyparts_t> bodyParts, int desiredRootLOD){
        int rootLOD = desiredRootLOD;

        if (header.numallowedrootlods > 0 && rootLOD >= header.numallowedrootlods){
            rootLOD = header.numallowedrootlods - 1;
        }

        int vertexOffset = 0;
        for (int bpID = 0; bpID < bodyParts.size(); bpID++){
            mstudiobodyparts_t bodyPart = bodyParts.get(bpID);
            for (int mdID = 0; mdID < bodyPart.models.size(); mdID++){
                mstudiomodel_t model = bodyPart.models.get(mdID);

                int totalMeshVertices = 0;
                for (int meshID = 0; meshID < model.meshes.size(); meshID++){
                    mstudiomesh_t mesh = model.meshes.get(meshID);

                    mesh.numvertices = mesh.vertexdata.numLODVertexes[rootLOD];
                    mesh.vertexoffset = totalMeshVertices;
                    totalMeshVertices += mesh.numvertices;
                }

                model.numvertices = totalMeshVertices;
                model.vertexindex = vertexOffset;
                vertexOffset += totalMeshVertices;
            }
        }

        return rootLOD;
    }

    private void populateBuffers(mstudiovertex_t mVertex, Vector4f mTangent, int vertexArrayOffset,
                                 FloatBuffer positions, FloatBuffer normals, FloatBuffer uv0s, FloatBuffer tangents,
                                 FloatBuffer boneWeights, IntBuffer boneIDs) {
        int positionOffset = vertexArrayOffset * 3;
        positions.put(positionOffset, mVertex.m_vecPosition.contents);

        int normalOffset = vertexArrayOffset * 3;
        normals.put(normalOffset, mVertex.m_vecNormal.contents);

        int uv0Offset = vertexArrayOffset * 2;
        uv0s.put(uv0Offset, mVertex.m_vecTexCoord.x)
            .put(uv0Offset + 1, mVertex.m_vecTexCoord.y);

        int tangentOffset = vertexArrayOffset * 4;
        tangents.put(tangentOffset, mTangent.x)
            .put(tangentOffset + 1, mTangent.y)
            .put(tangentOffset + 2, mTangent.z) //TODO you may need to invert z, you did it in your old code.
            .put(tangentOffset + 3, mTangent.w);

        int boneWeightOffset = vertexArrayOffset * 3;
        boneWeights.put(boneWeightOffset, mVertex.m_BoneWeights.weight[0])
            .put(boneWeightOffset + 1, mVertex.m_BoneWeights.weight[1])
            .put(boneWeightOffset + 2, mVertex.m_BoneWeights.weight[2]);

        int boneIDOffset = vertexArrayOffset * 4;
        boneIDs.put(boneIDOffset, mVertex.m_BoneWeights.bone[0])
            .put(boneIDOffset + 1, mVertex.m_BoneWeights.bone[1])
            .put(boneIDOffset + 2, mVertex.m_BoneWeights.bone[2])
            .put(boneIDOffset + 3, mVertex.m_BoneWeights.numbones);
    }

    @Override
    public HyModel loadResource(String handle) {
        HyModel model = load(HyFile.get(handle));
        model.setRetrievalHandle(handle);
        model.setAssetType(SourceAssetTypes.MDL);
        model.setLoaded(true);
        return model;
    }

    @Override
    public void preload() {
        //load the default asset
    }

    @Override
    public void unloadDefaults() {

    }

    @Override
    public HyModel supplyDefault() {
        //some kind of default model that i don't have yet. could do internal. if the handle is null is that bad? i don't like null for these kinds of things.
        //TODO Assimp integration for standard model loader
        //TODO stb for standard image loader

        return null;
    }

    @Override
    public AssetLoadTask beginAssetLoad() {
        return null;
    }

    @Override
    public void mainAssetLoad(AssetLoadTask aTask) {

    }

    @Override
    public void setAssetContents(ByteBuffer source) {

    }

    @Override
    public void setAssetContents(HyModel source) {

    }
}
