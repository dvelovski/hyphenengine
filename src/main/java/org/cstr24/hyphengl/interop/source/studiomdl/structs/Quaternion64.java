package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.joml.QuaternionfX;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Quaternion64 extends BaseStruct implements StructWrapper<Quaternion64>{
    public static final int SIZE = 8;
    public boolean wNeg;

    public QuaternionfX value;

    static int count = 0;

    public Quaternion64 parse(ByteBuffer in) {
        /*byte[] qBytes = new byte[8];
        in.get(structPos, qBytes);
        */
        short[] qBytes = new short[8];
        for (int i = 0; i < 8; i++){
            qBytes[i] = uByteToShort(in.get(structPos + i));
        }

        int cX = (127 & qBytes[7]) << 14 | qBytes[6] << 6 | (252 & qBytes[5]) >> 2; //compressed x
        int cY = (3 & qBytes[5]) << 19 | qBytes[4] << 11 | qBytes[3] << 3 | (224 & qBytes[2]) >> 5; //compressed y
        int cZ = (31 & qBytes[2]) << 16 | qBytes[1] << 8 | qBytes[0]; //compressed z
        wNeg = ((128 & qBytes[7]) >> 7) != 0;

        float x, y, z, w;
        x = (cX - 1048576) * (1 / 1048576.5f);
        y = (cY - 1048576) * (1 / 1048576.5f);
        z = (cZ - 1048576) * (1 / 1048576.5f);
        w = (float) Math.sqrt(1 - x * x - y * y - z * z);
        if (wNeg){
            w = -w;
        }

        value = new QuaternionfX(x, y, z, w);

        //System.out.println("q64: " + (++count) + " compressed vals: " + cX + ", " + cY + ", " + cZ + " and wneg: " + (wNeg));
        //System.out.println("decompressed vals: " + x + ", " + y + ", " + z + ", " + w);

        return this;
    }

    public QuaternionfX toQuaternion(){
        return value;
    }

    public int sizeOf() {
        return SIZE;
    }
}
