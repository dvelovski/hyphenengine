package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class mstudiolinearbone_t extends BaseStruct implements StructWrapper<mstudiolinearbone_t> {
    public int numbones;
    private int flagsIndex;
    private int parentIndex;
    private int posIndex;
    private int quatIndex;
    private int rotIndex;
    private int poseToBoneIndex;
    private int posScaleIndex;
    private int rotScaleIndex;
    private int qAlignmentIndex;


    @Override
    public mstudiolinearbone_t parse(ByteBuffer in) {
        numbones = in.getInt();
        flagsIndex = in.getInt();
        parentIndex = in.getInt();
        posIndex = in.getInt();
        quatIndex = in.getInt();
        rotIndex = in.getInt();
        poseToBoneIndex = in.getInt();
        posScaleIndex = in.getInt();
        rotScaleIndex = in.getInt();
        qAlignmentIndex = in.getInt();

        skip(in, Integer.SIZE * 6); //int unused[6];
        return this;
    }

    @Override
    public int sizeOf() {
        return 64;
    }

    public int flags(ByteBuffer in, int index){
        return fetchInt(in, this.structPos + flagsIndex + (index * Integer.BYTES));
    }
    public int parent(ByteBuffer in, int index){
        return fetchInt(in, this.structPos + parentIndex + (index * Integer.BYTES));
    }

    public vector_t pos(ByteBuffer in, int index){
        int target = this.structPos + posIndex + (index * vector_t.SIZE);
        return fetchVector(in, target);
    }

    public vector_t posScale(ByteBuffer in, int index){
        int target = this.structPos + posScaleIndex + (index * vector_t.SIZE);
        return fetchVector(in, target);
    }
    public RadianEuler rot(ByteBuffer in, int index){
        int target = this.structPos + rotIndex + (index * RadianEuler.SIZE);
        return fetchRadianEuler(in, target);
    }

    public vector_t rotScale(ByteBuffer in, int index){
        int target = this.structPos + rotScaleIndex + (index * vector_t.SIZE);
        return fetchVector(in, target);
    }
    public Quaternion quat(ByteBuffer in, int index){
        int target = this.structPos + quatIndex + (index * Quaternion.SIZE);
        return fetchQuaternion(in, target);
    }

    public Quaternion qAlignment(ByteBuffer in, int index){
        int target = this.structPos + qAlignmentIndex + (index * Quaternion.SIZE);
        return fetchQuaternion(in, target);
    }

}
