package org.cstr24.hyphengl.interop.source.studiomdl.structs.vvd;

import org.cstr24.hyphengl.filesystem.HyFile;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.stream.IntStream;

public class VVDParser {
    public VVDParser(){

    }
    public VVDFile parse(HyFile in){
        try {
            var fileBuffer = in.mapFile().order(ByteOrder.LITTLE_ENDIAN);

            vertexFileHeader_t vtxHeader = new vertexFileHeader_t();
            vtxHeader.parseStruct(fileBuffer);

            var vvdFile = new VVDFile(vtxHeader);

            fileBuffer.position(vtxHeader.fixupTableStart);
            for (int fUp = 0; fUp < vtxHeader.numFixups; fUp++){
                var fixup = new vertexFileFixup_t().parseStruct(fileBuffer);
                vvdFile.fixups.add(fixup);
            }

            int vertexCount = IntStream.of(vtxHeader.numLODVertexes).sum();
            //System.out.println("Total vertex count: " + vertexCount + " / vertex counts: " + Arrays.toString(vtxHeader.numLODVertexes));

            fileBuffer.position(vtxHeader.vertexDataStart);

            while (fileBuffer.position() < vtxHeader.tangentDataStart){
                var vertex = new mstudiovertex_t().parseStruct(fileBuffer);
                vvdFile.vertices.add(vertex);
            }

            while (fileBuffer.position() < fileBuffer.limit()){
                vvdFile.tangents.add(
                        new Vector4f(
                                fileBuffer.getFloat(), //x
                                fileBuffer.getFloat(), //y
                                fileBuffer.getFloat(), //z
                                fileBuffer.getFloat()  //w
                        )
                );
            }

            //run fixups in StudioModelLoader
            return vvdFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
