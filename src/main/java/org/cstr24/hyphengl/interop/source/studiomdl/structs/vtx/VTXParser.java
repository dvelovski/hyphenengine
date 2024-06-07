package org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.studiomdl.StudioModel;

import java.io.IOException;
import java.nio.ByteOrder;

public class VTXParser {
    public int mdlVersion;
    public VTXParser(){
        this(StudioModel.CURRENT_STUDIO_VERSION);
    }
    public VTXParser(int _mdlVersion){
        this.mdlVersion = _mdlVersion;
    }

    public VTXFile parse(HyFile in){
        try {
            //System.out.println("Parsing VTX " + in.getFilePath().toString() + " with version " + mdlVersion);
            var fileBuffer = in.mapFile().order(ByteOrder.LITTLE_ENDIAN);

            FileHeader_t vtxHeader = new FileHeader_t();
            vtxHeader.parseStruct(fileBuffer);

            var vtxFile = new VTXFile(vtxHeader);

            int l_bodyPartOffset = vtxHeader.bodyPartOffset;
            fileBuffer.position(vtxHeader.bodyPartOffset);

            //now, let's do some ugly nested loops
            for (int bP = 0; bP < vtxHeader.numBodyParts; bP++){
                var bodyPart = new BodyPartHeader_t().parseStruct(fileBuffer, l_bodyPartOffset);
                int l_ModelHeaderOffset = bodyPart.modelOffset + l_bodyPartOffset;

                for (int mD = 0; mD < bodyPart.numModels; mD++){
                    var modelHeader = new ModelHeader_t().parseStruct(fileBuffer, l_ModelHeaderOffset);
                    int l_ModelLODOffset = l_ModelHeaderOffset + modelHeader.lodOffset;

                    for (int lD = 0; lD < modelHeader.numLODs; lD++){
                        var modelLOD = new ModelLODHeader_t().parseStruct(fileBuffer, l_ModelLODOffset);
                        int l_lodMeshOffset = l_ModelLODOffset + modelLOD.meshOffset;

                        for (int mS = 0; mS < modelLOD.numMeshes; mS++){
                            var LODMeshHeader = new MeshHeader_t().parseStruct(fileBuffer, l_lodMeshOffset);
                            int l_stripGroupHeaderOffset = l_lodMeshOffset + LODMeshHeader.stripGroupHeaderOffset;

                            for (int sG = 0; sG < LODMeshHeader.numStripGroups; sG++){
                                StripGroupHeader_t stripGroupHeader = (mdlVersion >= 49 ? new StripGroupHeader_t_v49() : new StripGroupHeader_t());
                                stripGroupHeader.parseStruct(fileBuffer, l_stripGroupHeaderOffset);
                                int l_stripHeaderOffset = l_stripGroupHeaderOffset + stripGroupHeader.stripOffset;

                                for (int sH = 0; sH < stripGroupHeader.numStrips; sH++){
                                    StripHeader_t stripHeader = (mdlVersion >= 49 ? new StripHeader_t_v49() : new StripHeader_t());
                                    stripHeader.parseStruct(fileBuffer, l_stripHeaderOffset);

                                    stripGroupHeader.stripHeaders.add(stripHeader);
                                    l_stripHeaderOffset += stripHeader.sizeOf();
                                }

                                fileBuffer.position(l_stripGroupHeaderOffset + stripGroupHeader.vertOffset);
                                for (int vtx = 0; vtx < stripGroupHeader.numVerts; vtx++){
                                    stripGroupHeader.vertices.add(new Vertex_t().parseStruct(fileBuffer));
                                }

                                fileBuffer.position(l_stripGroupHeaderOffset + stripGroupHeader.indexOffset);
                                for (int idx = 0; idx < stripGroupHeader.numIndicies; idx++){
                                    stripGroupHeader.indices.add(fileBuffer.getShort());
                                }

                                LODMeshHeader.stripGroupHeaders.add(stripGroupHeader);
                                l_stripGroupHeaderOffset += stripGroupHeader.sizeOf();
                            }

                            modelLOD.meshHeaders.add(LODMeshHeader);
                            l_lodMeshOffset += MeshHeader_t.SIZE;
                        }

                        modelHeader.modelLODs.add(modelLOD);
                        l_ModelLODOffset += ModelLODHeader_t.SIZE;
                    }

                    bodyPart.models.add(modelHeader);
                    l_ModelHeaderOffset += ModelHeader_t.SIZE;
                }

                vtxFile.bodyParts.add(bodyPart);
                l_bodyPartOffset += BodyPartHeader_t.SIZE;
            }

            fileBuffer.position(l_bodyPartOffset);

            return vtxFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
