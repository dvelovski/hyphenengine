package org.cstr24.hyphengl.interop.source;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.cstr24.hyphengl.data.ComponentType;
import org.cstr24.hyphengl.data.GL46ImmutableBuffer;
import org.cstr24.hyphengl.data.InterleavedBuffer;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.geometry.*;
import org.cstr24.hyphengl.interop.source.kv.KeyValueParser;
import org.cstr24.hyphengl.interop.source.kv.KeyValueTree;
import org.cstr24.hyphengl.interop.source.materials.SourceMaterial;
import org.cstr24.hyphengl.interop.source.pak.PAKFileSystem;
import org.cstr24.hyphengl.interop.source.structs.vector_t;
import org.cstr24.hyphengl.interop.source.vbsp.BSPLoader;
import org.cstr24.hyphengl.interop.source.vbsp.Constants;
import org.cstr24.hyphengl.interop.source.vbsp.structs.*;
import org.cstr24.hyphengl.rendering.surfaces.HyMaterial;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;
import org.cstr24.hyphengl.rendering.surfaces.MaterialManager;
import org.cstr24.hyphengl.utils.ListUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class BSPMapFile {
    private short[][] mapClusterBitsets;

    public static final int LUMP_ENTITIES = 0;
    public static final int LUMP_PLANES = 1;
    public static final int LUMP_TEXDATA = 2;
    public static final int LUMP_VERTEXES = 3;
    public static final int LUMP_VISIBILITY = 4;
    public static final int LUMP_NODES = 5;
    public static final int LUMP_TEXINFO = 6;
    public static final int LUMP_FACES = 7;
    public static final int LUMP_LIGHTING = 8;
    public static final int LUMP_OCCLUSION = 9;
    public static final int LUMP_LEAFS = 10;
    public static final int LUMP_FACEIDS = 11;
    public static final int LUMP_EDGES = 12;
    public static final int LUMP_SURFEDGES = 13;
    public static final int LUMP_MODELS = 14;
    public static final int LUMP_WORLDLIGHTS = 15;
    public static final int LUMP_LEAFFACES = 16;
    public static final int LUMP_LEAFBRUSHES = 17;
    public static final int LUMP_BRUSHES = 18;
    public static final int LUMP_BRUSHSIDES = 19;
    public static final int LUMP_AREAS = 20;
    public static final int LUMP_AREAPORTALS = 21;

    public static final int LUMP_PORTALS = 22;
    public static final int LUMP_UNUSED0 = 22; //Source 2007
    public static final int LUMP_PROPCOLLISION = 22; //Source 2009

    public static final int LUMP_CLUSTERS = 23;
    public static final int LUMP_UNUSED1 = 23; //Source 2007
    public static final int LUMP_PROPHULLS = 23; //Source 2009

    public static final int LUMP_PORTALVERTS = 24;
    public static final int LUMP_UNUSED2 = 24; //Source 2007, Unused
    public static final int LUMP_PROPHULLVERTS = 24; //Source 2009

    public static final int LUMP_CLUSTERPORTALS = 25;
    public static final int LUMP_UNUSED3 = 25; //Source 2007
    public static final int LUMP_PROPTRIS = 25; //Source 2009

    public static final int LUMP_DISPINFO = 26;

    public static final int LUMP_ORIGINALFACES = 27;
    public static final int LUMP_PHYSDISP = 28;
    public static final int LUMP_PHYSCOLLIDE = 29;
    public static final int LUMP_VERTNORMALS = 30;
    public static final int LUMP_VERTNORMALINDICES = 31;
    public static final int LUMP_DISP_LIGHTMAP_ALPHAS = 32;
    public static final int LUMP_DISP_VERTS = 33;
    public static final int LUMP_DISP_LIGHTMAP_SAMPLE_POSITIONS = 34;
    public static final int LUMP_GAME_LUMP = 35;
    public static final int LUMP_LEAFWATERDATA = 36;
    public static final int LUMP_PRIMITIVES = 37;
    public static final int LUMP_PRIMVERTS = 38;
    public static final int LUMP_PRIMINDICES = 39;
    public static final int LUMP_PAKFILE = 40;
    public static final int LUMP_CLIPPORTALVERTS = 41;
    public static final int LUMP_CUBEMAPS = 42;
    public static final int LUMP_TEXDATA_STRING_DATA = 43;
    public static final int LUMP_TEXDATA_STRING_TABLE = 44;
    public static final int LUMP_OVERLAYS = 45;
    public static final int LUMP_LEAFMINDISTTOWATER = 46;
    public static final int LUMP_FACE_MACRO_TEXTURE_INFO = 47;
    public static final int LUMP_DISP_TRIS = 48;

    public static final int LUMP_PHYSCOLLIDESURFACE = 49; //Compressed win32-specific Havok terrain surface collision data
    public static final int LUMP_PROP_BLOB = 49; //Source 2007

    public static final int LUMP_WATEROVERLAYS = 50;

    public static final int LUMP_LIGHTMAPPAGES = 51;
    public static final int LUMP_LEAF_AMBIENT_INDEX_HDR =  51; //Source 2007

    public static final int LUMP_LIGHTMAPPAGEINFOS = 52;
    public static final int LUMP_LEAF_AMBIENT_INDEX = 52; //Source 2007

    public static final int LUMP_LIGHTING_HDR = 53;

    public static final int LUMP_WORLDLIGHTS_HDR = 54;
    public static final int LUMP_LEAF_AMBIENT_LIGHTING_HDR = 55;
    public static final int LUMP_LEAF_AMBIENT_LIGHTING = 56;
    public static final int LUMP_XZIPPAKFILE = 57;
    public static final int LUMP_FACES_HDR = 58;
    public static final int LUMP_MAP_FLAGS = 59;
    public static final int LUMP_OVERLAY_FADES = 60;
    public static final int LUMP_OVERLAY_SYSTEM_LEVELS = 61;
    public static final int LUMP_PHYSLEVEL = 62;
    public static final int LUMP_DISP_MULTIBLEND = 63;

    public ArrayList<dplane_t> mapPlanes = new ArrayList<>();
    public ArrayList<vector_t> mapVertices = new ArrayList<>();
    public ArrayList<dedge_t> mapEdges = new ArrayList<>();
    public ArrayList<surfedge_t> mapSurfedges = new ArrayList<>();
    public ArrayList<? extends dface_t> mapFaces = new ArrayList<>();
    public ArrayList<? extends dleaf_t> mapLeaves = new ArrayList<>();
    public ArrayList<Integer> mapLeafFaces = new ArrayList<>();
    public ArrayList<dmodel_t> mapModels = new ArrayList<>();
    public ArrayList<dnode_t> mapNodes = new ArrayList<>();

    public ArrayList<ddispinfo_t> mapDisplacements = new ArrayList<>();
    public ArrayList<dDispTri> mapDispTris = new ArrayList<>();
    public ArrayList<dDispVert> mapDispVerts = new ArrayList<>();

    public ArrayList<texinfo_t> mapTexInfos = new ArrayList<>();
    public ArrayList<texdata_t> mapTexDatas = new ArrayList<>();
    public ArrayList<String> mapTextureNames = new ArrayList<>();

    public HashMap<Integer, String> faceMaterialMappings = new HashMap<>();
    public HashMap<String, MaterialInstance> mapMaterials = new HashMap<>();

    public String mapPath = "";
    public String mapName = "";

    private BSPLoader mapLoader;

    private BSPMapFile(){

    }
    public BSPMapFile(BSPLoader _mapLoader) throws Exception {
        this.mapLoader = _mapLoader;
        if (mapLoader.load()){
            this.mapPath = mapLoader.getBSPPath().toString();
            this.mapName = mapLoader.getBSPPath().getFileName().toString();
        }else{
            System.out.println("Failed to load!");
        }
    }

    public void loadMapData() throws Exception{
        this.mapPlanes = mapLoader.deserializeLumpData(LUMP_PLANES, dplane_t.class);
        this.mapVertices = mapLoader.deserializeLumpData(LUMP_VERTEXES, vector_t.class);

        this.mapEdges = mapLoader.deserializeLumpData(LUMP_EDGES, dedge_t.class);
        this.mapSurfedges = mapLoader.deserializeLumpData(LUMP_SURFEDGES, surfedge_t.class);

        loadFaces();
        loadLeafLump();
        loadLeafFaces();

        this.mapModels = mapLoader.deserializeLumpData(LUMP_MODELS, dmodel_t.class);
        /*mapModels.forEach(dmodel -> {
            System.out.println(dmodel.firstFace + " - " + dmodel.numFaces);
        });*/

        loadVisibility();
        this.mapNodes = mapLoader.deserializeLumpData(LUMP_NODES, dnode_t.class);

        this.mapDisplacements = mapLoader.deserializeLumpData(LUMP_DISPINFO, ddispinfo_t.class);
        this.mapDispVerts = mapLoader.deserializeLumpData(LUMP_DISP_VERTS, dDispVert.class);
        this.mapDispTris = mapLoader.deserializeLumpData(LUMP_DISP_TRIS, dDispTri.class);

        this.mapTexInfos = mapLoader.deserializeLumpData(LUMP_TEXINFO, texinfo_t.class);
        this.mapTexDatas = mapLoader.deserializeLumpData(LUMP_TEXDATA, texdata_t.class);

        loadTextureNames();
    }
    public boolean mountPAKFile() throws Exception {
        ByteBuffer pakData = mapLoader.getLumpBuffer(LUMP_PAKFILE);

        PAKFileSystem pakFS = new PAKFileSystem(pakData);
        pakFS.setPakPath(mapLoader.getBSPPath());
        pakFS.loadAndMount(10);

        return false;
    }

    public void loadFaces() throws Exception {
        var faceLump = mapLoader.getLump(LUMP_FACES);
        Class<? extends dface_t> faceStructToUse;
        switch (mapLoader.getMapVersion()){
            case 17 -> {
                //System.out.println(">using BSP v17 dface_t format");
                faceStructToUse = dface_t_v17.class;
            }
            case 18 -> {
                //System.out.println(">using BSP v18 dface_t format");
                faceStructToUse = dface_t_v18.class;
            }
            default -> {
                //System.out.println(">using regular dface_t derivative");
                faceStructToUse = dface_t.class;
            }
        }
        this.mapFaces = mapLoader.deserializeLumpData(faceLump, faceStructToUse);
    }

    public void loadLeafLump() throws Exception {
        var leafLump = mapLoader.getLump(LUMP_LEAFS);

        Class<? extends dleaf_t> leafStructToUse;
        if (mapLoader.getMapVersion() == 19 && leafLump.version == 0){
            //System.out.println(">using HL2 leaf struct - v19");
            leafStructToUse = dleaf_t_v19.class;
        }else{
            //System.out.println(">using dleaf_t_all class");
            leafStructToUse = dleaf_t_all.class;
        }
        this.mapLeaves = mapLoader.deserializeLumpData(leafLump, leafStructToUse);
    }
    public void loadLeafFaces() throws Exception {
        var leafFaceLump = mapLoader.getLump(LUMP_LEAFFACES);
        var leafFaceReader = mapLoader.getLumpBuffer(LUMP_LEAFFACES);

        while (leafFaceReader.position() < leafFaceReader.limit()){
            mapLeafFaces.add(leafFaceReader.getShort() & 0xFFFF);
        }
        //System.out.println("leaf face count: " + mapLeafFaces.size());
    }

    public void loadVisibility() throws Exception {
        var visibilityBuffer = mapLoader.getLumpBuffer(LUMP_VISIBILITY);
        dvis_t dVisObject = new dvis_t().parse(visibilityBuffer);
//        System.out.println(dVisObject.numClusters);
//        System.out.println(Arrays.toString(dVisObject.byteOffsets));
//
//        String numClustersPre = Integer.toBinaryString(dVisObject.numClusters);
//        String numClustersPost = Integer.toBinaryString(dVisObject.numClusters + 7);
//        String numClustersPost2 = Integer.toBinaryString((dVisObject.numClusters + 7) >>> 3);
//        System.out.println("Binary int comparisons:");
//        System.out.println(numClustersPre + " | " + dVisObject.numClusters);
//        System.out.println(numClustersPost + " | " + (dVisObject.numClusters + 7));
//        System.out.println(numClustersPost2 + " | " + ((dVisObject.numClusters + 7) >>> 3));

        //decompress it
        short[][] clusterBitsets = new short[dVisObject.numClusters][];

        for (int i = 0; i < dVisObject.numClusters; i++){
            clusterBitsets[i] = new short[Constants.MAX_MAP_NODES >> 3];

            int in = dVisObject.byteOffsets[i][0];
            int iOut = 0; //other code (i.e. CHUF) gets the pointer to the array which they increment
            //I will use and increment indices

            do {
                short data = (short) (visibilityBuffer.get(in) & 0xFF);
                if (data != 0){
                    clusterBitsets[i][iOut++] = data;
                    in++;

                    continue;
                }
                short c = (short) (visibilityBuffer.get(in + 1) & 0xFF); //UInt8 c = in[1]

                in += 2; //skip the count byte

                while (c > 0){
                    clusterBitsets[i][iOut++] = 0;
                    c--;
                }

            } while (iOut < (dVisObject.numClusters + 7) >> 3);
        }

        this.mapClusterBitsets = clusterBitsets;
    }

    public void loadTextureNames() throws Exception { //they're not necessarily textures. they can be materials too, apparently.
        var texDataLump = mapLoader.getLump(LUMP_TEXDATA_STRING_DATA);
        var texDataBuffer = mapLoader.getLumpBuffer(LUMP_TEXDATA_STRING_DATA);

        int trueLumpSize = texDataBuffer.limit();
        byte[] stringData = new byte[trueLumpSize];
        texDataBuffer.get(stringData);

        var texTableLump = mapLoader.getLump(LUMP_TEXDATA_STRING_TABLE);
        var texTableBuffer = mapLoader.getLumpBuffer(LUMP_TEXDATA_STRING_TABLE);

        int texTableNumEntries = texTableBuffer.limit() / 4; //it's made up of ints
        //INFO 25-04 - have switched these methods to use the buffer's limit instead of declared file size, as fileLen can refer to the compressed size
        mapTextureNames = new ArrayList<>(texTableNumEntries);

        entryLoop:
        for (int i = 0; i < texTableNumEntries; i++){
            int strOffset = texTableBuffer.getInt();
            int ofsNull;

            for (ofsNull = strOffset; ofsNull < stringData.length; ofsNull++){
                if (stringData[ofsNull] == 0){
                    var str = (new String(
                        stringData,
                        strOffset,
                        ofsNull - strOffset)
                    );
                    System.out.println(str);
                    mapTextureNames.add(str);
                    continue entryLoop;
                }
            }
        }
    }

    public void createMapMaterials() throws IOException {
        int notFound = 0;
        int vmtCount = 0;
        int rawVTFCount = 0;
        int patchWithoutInclude = 0;
        Set<String> materialsFound = new HashSet<>();

        mapMaterials.clear();

        for (String path : mapTextureNames){
            String correctedPath = "materials/" + path;

            //METHOD ONE, strip vmt extension
            if (path.endsWith(".vmt")){
                correctedPath = correctedPath.substring(0, correctedPath.lastIndexOf('.'));
            }
            var matchingPaths = HyFile.matchesByPath(correctedPath); //matchesByPath now by default matches filename. which will kill off our issue with getting the wrong file because it CONTAINS the search string

            System.out.println("Searching for: " + correctedPath + " (old path: " + path + ") - matches: " + matchingPaths);

            HyFile fileToUse = null;
            int[] fileType = {0}; //VMT

            if (matchingPaths.isEmpty()){
                
            }else if (matchingPaths.size() == 1){
                fileToUse = matchingPaths.get(0);
            }else{
                //prioritize a VMT
                fileToUse = matchingPaths.stream().filter(
                    file -> file.getFileExtension().equalsIgnoreCase(".vmt")
                ).findAny().orElseGet(() -> {
                    fileType[0] = 1; //VTF
                    return matchingPaths.get(0);
                });
            }

            System.out.println("heuristic has determined: " + fileToUse);
            notFound = (fileToUse == null ? notFound + 1 : notFound);

            if (fileToUse != null){
                if (fileType[0] == 0){
                    vmtCount++;
                    String materialNameToUse = "";

                    KeyValueParser parser = new KeyValueParser();
                    KeyValueTree vmtTree = null;
                    try {
                        vmtTree = parser.parse(fileToUse);
                        System.out.println("\tMaterial type: " + vmtTree.treeRoot.name);


                        if (vmtTree.treeRoot.name.equalsIgnoreCase("patch")){
                            var included = vmtTree.treeRoot.getChild("include");
                            System.out.println("\tPatch data - " + included);
                            if (included == null){
                                patchWithoutInclude++;
                            }else{
                                ArrayList<HyFile> patchSearch = HyFile.matchesByPath(included.getValue(), HyFile.DEFAULT_SEARCH_FLAGS | HyFile.SEARCH_INCLUDE_EXTENSION);
                                if (!patchSearch.isEmpty()){
                                    KeyValueTree includedTree = parser.parse(patchSearch.get(0));
                                    System.out.println("\tPatching existing material: " + includedTree.treeRoot.name);

                                    materialNameToUse = includedTree.treeRoot.name;
                                    vmtTree = includedTree;
                                }else{
                                    notFound++;
                                }
                            }
                        }else{
                            materialNameToUse = vmtTree.treeRoot.name;
                        }

                        materialsFound.add(materialNameToUse);

                        if (materialNameToUse.equalsIgnoreCase("lightmappedgeneric")){
                            materialNameToUse = "LightmappedGeneric";
                        }
                        if (materialNameToUse.equalsIgnoreCase("unlitgeneric")){
                            materialNameToUse = "UnlitGeneric";
                        }
                        if (materialNameToUse.equalsIgnoreCase("worldvertextransition")){
                            materialNameToUse = "WorldVertexTransition";
                        }

                        HyMaterial material = MaterialManager.get().getMaterial(materialNameToUse);
                        SourceMaterial sourceMat = (SourceMaterial) material;

                        if (sourceMat != null){
                            System.out.println("\tWe have a matching material: " + material.materialName);
                            var matInstance = sourceMat.parseInstance(vmtTree);
                            mapMaterials.put(correctedPath, matInstance);
                        }else{
                            var inst = MaterialManager.get().getMaterial("LightmappedGeneric").createInstance();
                            mapMaterials.put(correctedPath, inst);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{
                    rawVTFCount++;
                }
            }else{
                System.out.println("File was null.");
                var inst = MaterialManager.get().getMaterial("LightmappedGeneric").createInstance();
                mapMaterials.put(correctedPath, inst);
            }
        }

        System.out.println("Failed to locate: " + notFound + " files.");
        System.out.println("VMT count: " + vmtCount + " | VTF count: " + rawVTFCount);
        System.out.println("Materials: " + materialsFound);
        System.out.println("Patched materials without an #include: " + patchWithoutInclude);
    }

    public HyMesh createMapMesh(){
        long start = System.currentTimeMillis();

        HyMesh mapMesh = MeshFactory.create();
        var faceBuffer = new GL46ImmutableBuffer();
        var elementBuffer = new GL46ImmutableBuffer();

        mapMesh.setVertexLayout(VertexLayout.with(
            VertexElement.of(ComponentType.Float, 3),//,    //Positions
            VertexElement.of(ComponentType.Float, 3),     //Normals
            VertexElement.of(ComponentType.Float, 2),    //UV0
            VertexElement.of(ComponentType.Float, 2)
        ));

        var mpMeshVertices = new ArrayList<Vector3f>();
        var mpMeshNormals = new ArrayList<Vector3f>();
        var mpMeshUV0 = new ArrayList<Vector2f>();
        var mpMeshUV1 = new ArrayList<Vector2f>();
        var mpMeshIndices = new ArrayList<Integer>();

        int indexOffset = 0;
        int elemBaseOffset = 0;

        for (int faceIdx = 0; faceIdx < mapModels.get(0).numFaces; faceIdx++) {
            dface_t face = mapFaces.get(faceIdx);

            var intermediateFace = processFace(face);
            int faceIndexCount = intermediateFace.indices.size();

            mpMeshVertices.addAll(intermediateFace.vertices);
            mpMeshNormals.addAll(intermediateFace.normals);
            mpMeshUV0.addAll(intermediateFace.uv0s);
            mpMeshUV1.addAll(intermediateFace.uv1s);
            mpMeshIndices.addAll(intermediateFace.indices);

            var faceSubMesh = SubMesh.create(
                Integer.toString(faceIdx), indexOffset, faceIndexCount
            ).elementBase(elemBaseOffset);

            String correctedReference = "materials/" + intermediateFace.textureDataReference;
            if (correctedReference.endsWith(".vmt")){
                correctedReference = correctedReference.substring(0, correctedReference.lastIndexOf('.'));
                //this only seems to happen with Water materials at the moment
            }
            //System.out.println("Searching for: " + correctedReference);

            if (mapMaterials.containsKey(correctedReference)){
                //System.out.println("Texture data reference for face: " + faceIdx + " = " + intermediateFace.textureDataReference);
                faceSubMesh.materialInstance = mapMaterials.get(correctedReference).clone();
            }else{
                System.out.println("Material data ref not found: " + correctedReference);
            }
            /*faceSubMesh.materialInstance = Engine.bspMaterial.createInstance();
            int textureIDToUse = Engine.missingTexture.getTextureID();
            //System.out.println("face texture ref: " + returned.textureDataReference);

            if (mapTextures.containsKey(intermediateFace.textureDataReference)){
                var tex = mapTextures.get(intermediateFace.textureDataReference);
                if (tex != null){
                    //System.out.println(tex);
                    textureIDToUse = tex.getTextureID();
                }else{
                    System.out.println("texture not found: " + intermediateFace.textureDataReference);
                }
            }else{
                System.out.println("Looking for texture: " + intermediateFace.textureDataReference + " which was not found.");
            }

            faceSubMesh.materialInstance.setProperty("baseTexture", textureIDToUse);*/
            mapMesh.addSubmesh(faceSubMesh);

            //System.out.println(faceSubMesh.elementID + " - " + intermediateFace.textureDataReference);

            indexOffset += intermediateFace.indices.size(); //this is the offset of indices that we're looking at
            elemBaseOffset += intermediateFace.vertices.size(); //this is the offset that we apply to the vertex pointed to by a given element in 'indices'
        }

        var interleaved = new InterleavedBuffer(mapMesh.vertexLayout, mpMeshVertices.size());
        interleaved.interleaveVec3s(0, mpMeshVertices);
        interleaved.interleaveVec2s(2, mpMeshUV0);

        faceBuffer.setData(interleaved.backingBuffer);
        elementBuffer.setData(ListUtils.intArrayListToIntBuffer(mpMeshIndices));

        mapMesh.setVertexData(faceBuffer, mpMeshVertices.size());
        mapMesh.setElementBuffer(elementBuffer, IndexElementType.Int, mpMeshIndices.size());

        mapMesh.apply();

        long end = System.currentTimeMillis();

        System.out.println("Process took: " + (end - start) + " ms");

        return mapMesh;
    }

    public BSPIntermediateFace processFace(dface_t face){
        BSPIntermediateFace toReturn = new BSPIntermediateFace();

        ArrayList<Vector3f> originalVertices = new ArrayList<>();

        toReturn.vertices = new ArrayList<>();
        toReturn.normals = new ArrayList<>();
        toReturn.uv0s = new ArrayList<>();
        toReturn.uv1s = new ArrayList<>();
        toReturn.indices = new ArrayList<>();

        for (int v = 0; v < face.numEdges; v++){
            int[] currentEdge = mapEdges.get(
                    Math.abs(mapSurfedges.get(face.firstEdge + v).edgeIndex)
            ).indices;
            var point1 = mapVertices.get(currentEdge[0]).toVec3f();
            var point2 = mapVertices.get(currentEdge[1]).toVec3f();
            var pNormal = mapPlanes.get(face.planeNum).normal.toVec3f();

            var surfEdge = mapSurfedges.get(face.firstEdge + v);
            if (surfEdge.edgeIndex >= 0){
                if (!toReturn.vertices.contains(point1)){
                    toReturn.vertices.add(point1);
                    toReturn.normals.add(pNormal);
                }
                originalVertices.add(point1);

                if (!toReturn.vertices.contains(point2)){
                    toReturn.vertices.add(point2);
                    toReturn.normals.add(pNormal);
                }
                originalVertices.add(point2);
            }else{
                if (!toReturn.vertices.contains(point2)){
                    toReturn.vertices.add(point2);
                    toReturn.normals.add(pNormal);
                }
                originalVertices.add(point2);

                if (!toReturn.vertices.contains(point1)){
                    toReturn.vertices.add(point1);
                    toReturn.normals.add(pNormal);
                }
                originalVertices.add(point1);
            }
        }

        if (face.displacementInfo > -1){
            var dispInfo = mapDisplacements.get(face.displacementInfo);
            int power = (int) Math.round(Math.pow(2, dispInfo.power));

            ArrayList<Vector3f> dispVertices = new ArrayList<>();
            Vector3f startPosition = toReturn.vertices.get(0);
            Vector3f topCorner = toReturn.vertices.get(1);
            Vector3f topRightCorner = toReturn.vertices.get(2);
            Vector3f rightCorner = toReturn.vertices.get(3);

            Vector3f dispStartingVertex = dispInfo.startPosition.toVec3f();

            if (dispStartingVertex.distance(topCorner) < 0.01f){
                Vector3f tempCorner = startPosition;

                startPosition = topCorner;
                topCorner = topRightCorner;
                topRightCorner = rightCorner;
                rightCorner = tempCorner;
            }else if (dispStartingVertex.distance(rightCorner) < 0.01f){
                Vector3f tempCorner = startPosition;

                startPosition = rightCorner;
                rightCorner = topRightCorner;
                topRightCorner = topCorner;
                topCorner = tempCorner;
            }else if (dispStartingVertex.distance(topRightCorner) < 0.01f){
                Vector3f tempCorner = startPosition;

                startPosition = topRightCorner;
                topRightCorner = tempCorner;
                tempCorner = rightCorner;
                rightCorner = topCorner;
                topCorner = tempCorner;
            }

            int orderNum = 0;
            Vector3f leftSide = topCorner.sub(startPosition, new Vector3f());
            Vector3f rightSide = topRightCorner.sub(rightCorner, new Vector3f());
            float leftSideSegmentationDistance = leftSide.length() / power;
            float rightSideSegmentationDistance = rightSide.length() / power;

            for (int ln = 0; ln < (power + 1); ln++){
                for (int pt = 0; pt < (power + 1); pt++){
                    var leftPoint = new Vector3f(leftSide).normalize().mul(ln).mul(leftSideSegmentationDistance).add(startPosition);
                    var rightPoint = new Vector3f(rightSide).normalize().mul(ln).mul(rightSideSegmentationDistance).add(rightCorner);
                    var currentLine = new Vector3f(rightPoint).sub(leftPoint);
                    var pointDirection = new Vector3f(currentLine).normalize();

                    float pointSideSegmentationDistance = currentLine.length() / power;

                    var dispVertT = mapDispVerts.get(dispInfo.dispVertStart + orderNum);
                    var pointA = new Vector3f(pointDirection).mul(pointSideSegmentationDistance).mul(pt).add(leftPoint);
                    var dispDirectionA = dispVertT.vec.toVec3f();

                    var toAdd = new Vector3f(dispDirectionA).mul(dispVertT.dist).add(pointA);
                    dispVertices.add(toAdd);

                    orderNum++;
                }
            }

            toReturn.vertices = dispVertices;
        }

        if (face.displacementInfo > -1){
            var dispInfo = mapDisplacements.get(face.displacementInfo);
            int power = (int) Math.round(Math.pow(2, dispInfo.power));

            for (int row = 0; row < power; row++){
                for (int col = 0; col < power; col++){
                    int currLine = row * (power + 1);
                    int nextLineStart = (row + 1) * (power + 1);

                    toReturn.indices.add(currLine + col);
                    toReturn.indices.add(currLine + col + 1);
                    toReturn.indices.add(nextLineStart + col);

                    toReturn.indices.add(currLine + col + 1);
                    toReturn.indices.add(nextLineStart + col + 1);
                    toReturn.indices.add(nextLineStart + col);
                }
            }
        }else{
            for (int i = 0; i < (originalVertices.size() / 2); i++){
                int firstOrigIndex = i * 2;
                int secondOrigIndex = (i * 2) + 1;
                int thirdOrigIndex = 0;

                int firstIndex = toReturn.vertices.indexOf(originalVertices.get(firstOrigIndex));
                int secondIndex = toReturn.vertices.indexOf(originalVertices.get(secondOrigIndex));
                int thirdIndex = toReturn.vertices.indexOf(originalVertices.get(thirdOrigIndex));

                toReturn.indices.add(thirdIndex);
                toReturn.indices.add(secondIndex);
                toReturn.indices.add(firstIndex);
            }
        }

        for (int vtxCount = 0; vtxCount < toReturn.vertices.size(); vtxCount++){
            toReturn.normals.add(new Vector3f());
            toReturn.uv0s.add(new Vector2f());
        }

        var texInfo = mapTexInfos.get(face.textureInfo);
        var texData = mapTexDatas.get(texInfo.texData);
        float[][] texVecs = texInfo.textureVecs;
        float[][] lightVecs = texInfo.lightmapVecs;

        for (Integer idx : toReturn.indices){
            var vertex = toReturn.vertices.get(idx);
            var uv0 = toReturn.uv0s.get(idx);
            uv0.x = ((texVecs[0][0] * vertex.x) + (texVecs[0][1] * vertex.y) + (texVecs[0][2] * vertex.z) + texVecs[0][3]) / texData.viewWidth;
            uv0.y = ((texVecs[1][0] * vertex.x) + (texVecs[1][1] * vertex.y) + (texVecs[1][2] * vertex.z) + texVecs[1][3]) / texData.viewHeight;

            //uv0.y = 1f - uv0.y;
        }

        toReturn.textureDataReference = mapTextureNames.get(texData.nameStringTableID);

        for (Vector3f surfaceVertex : toReturn.vertices) {
            float temp = surfaceVertex.y;
            surfaceVertex.y = surfaceVertex.z;
            surfaceVertex.z = -temp;
        }

        return toReturn;
    }

    public int findLeaf(Vector3f inPos){
        int i = 0;
        float distance;

        while (i >= 0){
            var node = mapNodes.get(i);
            var plane = mapPlanes.get(node.planeNum);

            distance =
                    (plane.normal.x() * inPos.x  +
                     plane.normal.y() * inPos.y  +
                     plane.normal.z() * inPos.z) - plane.dist;

            i = (distance >= 0 ? node.children[0] : node.children[1]);
        }

        return ~i;
    }

    public boolean clusterVisible(int from, int toCheck){
        if (from < 0 || toCheck < 0 || from > mapClusterBitsets.length){
            return true;
        }else{
            return ((mapClusterBitsets[from][toCheck >> 3] & (1 << (toCheck & 7))) != 0);
        }
    }

    private static class BSPIntermediateFace {
        public ArrayList<Vector3f> vertices;
        public ArrayList<Vector3f> normals;
        public ArrayList<Vector2f> uv0s;
        public ArrayList<Vector2f> uv1s;
        public ArrayList<Integer> indices;
        public String textureDataReference = "";
    }
}
