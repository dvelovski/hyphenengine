package org.cstr24.hyphengl.interop.source.structs;

import org.cstr24.hyphengl.interop.source.studiomdl.structs.Quaternion;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.RadianEuler;

import java.nio.ByteBuffer;

public class BaseStruct {
    public int structPos;
    public void setStructPos(int position){
        structPos = position;
    }

    public int fetchInt(ByteBuffer in, int pos){
        int prePos = in.position();
        in.position(pos);
        int value = in.getInt();
        in.position(prePos);
        return value;
    }

    public vector_t fetchVector(ByteBuffer in, int pos){
        int prePos = in.position();
        vector_t result = new vector_t().parseStruct(in, pos);
        in.position(prePos);
        return result;
    }
    public Quaternion fetchQuaternion(ByteBuffer in, int pos){
        int prePos = in.position();
        Quaternion result = new Quaternion().parseStruct(in, pos);
        in.position(prePos);
        return result;
    }

    public RadianEuler fetchRadianEuler(ByteBuffer in, int pos){
        int prePos = in.position();
        RadianEuler result = new RadianEuler().parseStruct(in, pos);
        in.position(prePos);
        return result;
    }
}
