package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.joml.QuaternionfX;

import java.nio.ByteBuffer;

public class Quaternion48 extends BaseStruct implements StructWrapper<Quaternion48> {
    public static final int SIZE = 6;
    public boolean wNeg;

    static int count = 0;

    public float x;
    public float y;
    public float z;
    public float w;

    @Override
    public Quaternion48 parse(ByteBuffer in) {
        int cX = uShortToInt(in.getShort());
        int cY = uShortToInt(in.getShort());
        int zS = uShortToInt(in.getShort());
        boolean wNeg = (zS & 32768) != 0;

        x = ((cX - 32768) * (1 / 32768.0f));
        y = ((cY - 32768) * (1 / 32768.0f));
        z = 6103515625e-14f * ((32767 & zS) - 16384); //this float, since it's not explained anywhere in loadout.tf code, warrants a 'what the fuck' comment like q_rSqurt (i think that's its name?)

        w = (float) Math.sqrt(1 - (x * x) - (y * y) - (z * z));
        if (wNeg){
            w = -w;
        }

        System.out.println("q48: " + (++count) + " compressed vals: " + cX + ", " + cY + ", " + zS + " and wneg: " + (wNeg));
        System.out.println("decompressed vals: " + x + ", " + y + ", " + z + ", " + w);

        return this;
    }

    public QuaternionfX toQuaternion(){
        return new QuaternionfX(x, y, z, w);
    }

    @Override
    public int sizeOf() {
        return 6;
    }
}
