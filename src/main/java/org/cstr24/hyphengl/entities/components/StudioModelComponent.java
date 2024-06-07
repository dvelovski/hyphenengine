package org.cstr24.hyphengl.entities.components;

import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.kv.KeyValueParser;
import org.cstr24.hyphengl.interop.source.kv.KeyValueTree;
import org.cstr24.hyphengl.interop.source.materials.SourceMaterial;
import org.cstr24.hyphengl.interop.source.studiomdl.StudioModel;
import org.cstr24.hyphengl.interop.source.studiomdl.StudioModelSequenceInstance;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.*;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx.MeshHeader_t;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx.StripGroupHeader_t;
import org.cstr24.hyphengl.rendering.surfaces.HyMaterial;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;
import org.cstr24.hyphengl.rendering.surfaces.MaterialManager;
import org.joml.Matrix4f;

public class StudioModelComponent extends ModelComponent{
    public int skinFamily;

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public ModelComponent create() {
        return super.create();
    }

    @Override
    public ModelComponent cloneComponent() {
        return super.cloneComponent();
    }

    @Override
    public ModelComponent reset() {
        return super.reset();
    }

    @Override
    public String getComponentType() {
        return super.getComponentType();
    }

    @Override
    public String getComponentSimpleName() {
        return super.getComponentSimpleName();
    }

    @Override
    public Matrix4f[] getSkeleton() {
        if (!animationInstances.isEmpty()){
            ((StudioModelSequenceInstance) animationInstances.getFirst()).setupBones(mstudiobone_t.BONE_USED_BY_ANYTHING);
        }
        return super.getSkeleton();
    }

    public void setSkinFamily(int index){
        var sModel = ((StudioModel) modelHandle.get());

        if (index >= sModel.header.numskinfamilies){
            index = 0;
            //fall back
        }
        //System.out.println("\n***setting skin family " + index + " on instance of " + sModel.modelName + "***");

        int baseOffset = sModel.header.numskinref * index;
        int subMeshIndex = 0; //engine sub mesh index

        //go through the body parts
        for (int bp = 0; bp < sModel.bodyParts.size(); bp++) {
            mstudiobodyparts_t part = sModel.bodyParts.get(bp);
            for (int md = 0; md < part.nummodels; md++){
                mstudiomodel_t model = part.models.get(md);
                for (int ms = 0; ms < model.nummeshes; ms++){
                    mstudiomesh_t mesh = model.meshes.get(ms);
                    short sRef = sModel.skinRefs[baseOffset + mesh.material];

                    mstudiotexture_t skinTex = sModel.mStudioTextureTs.get(sRef);
                    //search the sModel's texture directories.
                    boolean found = false;
                    HyFile materialFile = null;

                    for (int txd = 0; txd < sModel.header.numcdtextures; txd++){
                        //System.out.println(">>> texture search attempt " + txd);
                        //System.out.println(">>> base textureName -> " + skinTex.textureName);
                        //System.out.println(">>> base textureDir -> " + sModel.modelTextureDirectories.get(txd));

                        String texturePath = constructPath(skinTex, sModel, txd);
                        //System.out.println(texturePath);
                        materialFile = HyFile.get(texturePath);

                        if (materialFile.exists()){
                            //System.out.println("found it: " + materialFile.getFilePath());
                            found = true;
                            break;
                        }
                    }
                    MaterialInstance instResult;
                    if (found){
                        instResult = loadMaterial(materialFile);
                    }else{
                        instResult = constructErrorMaterialInstance();
                    }

                    //TODO, only retain the VTX data I actually need, otherwise this is a huge waste of memory (yes it's several hundred KB at most but who can afford complacency!)
                    MeshHeader_t vtxMHeader = sModel.vtx.bodyParts.get(bp).models.get(md).modelLODs.getFirst().meshHeaders.get(ms);
                    for (int sg = 0; sg < vtxMHeader.numStripGroups; sg++){
                        StripGroupHeader_t sHeader = vtxMHeader.stripGroupHeaders.get(sg);
                        for (int st = 0; st < sHeader.numStrips; st++){
                            subMeshStates.get(subMeshIndex++).materialInstance = instResult;
                        }
                    }
                }
            }
        }
        this.skinFamily = index;

        System.out.println();
    }

    private String constructPath(mstudiotexture_t skinTex, StudioModel sModel, int dirIndex) {
        String textureName = skinTex.textureName.isBlank() ? SourceInterop.stripClass(sModel.modelName) : skinTex.textureName;
        String texSearchPath = sModel.modelTextureDirectories.get(dirIndex);

        if (texSearchPath.isBlank()){
            if (!textureName.contains("/")){
                texSearchPath = sModel.fileRef.getFilePath().getParent().toString();
                System.out.println("funky path construction possibility - trying " + texSearchPath + " as there isn't any directory information");
            }
        }

        return "materials\\" + texSearchPath + "\\" + textureName + ".vmt";
    }

    public MaterialInstance loadMaterial(HyFile matFile){
        KeyValueParser kvParser = new KeyValueParser();
        MaterialInstance instanceResult = null;

        try {
            KeyValueTree tree = kvParser.parse(matFile);
            String materialName = tree.treeRoot.name.toLowerCase();

            //System.out.println(materialName);

            HyMaterial baseMat = MaterialManager.get().getMaterial(materialName);
            if (baseMat != null){
                instanceResult = ((SourceMaterial) baseMat).parseInstance(tree);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (instanceResult == null){
            return constructErrorMaterialInstance();
        }
        return instanceResult;
    }
    public MaterialInstance constructErrorMaterialInstance(){
        return MaterialManager.get().getMaterial("VertexLitGeneric").createInstance();
    }
}
