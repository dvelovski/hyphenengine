package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.SourceInterop;
import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.joml.*;

import java.nio.ByteBuffer;

public class mstudiobone_t extends BaseStruct implements StructWrapper<mstudiobone_t> {
    public static final int BONE_CALCULATE_MASK = 0b11111;
    public static final int BONE_PHYSICALLY_SIMULATED = 1;
    public static final int BONE_PHYSICS_PROCEDURAL = 2;
    public static final int BONE_ALWAYS_PROCEDURAL = 4;
    public static final int BONE_SCREEN_ALIGN_SPHERE = 8;
    public static final int BONE_SCREEN_ALIGN_CYLINDER = 16;

    public static final int BONE_USED_MASK = 524032;
    public static final int BONE_USED_BY_ANYTHING = 0b1111111111100000000;
    public static final int BONE_USED_BY_HITBOX = 0x00000100;
    public static final int BONE_USED_BY_ATTACHMENT = 0x00000200;
    public static final int BONE_USED_BY_VERTEX_MASK = 0b111111110000000000;
    public static final int BONE_USED_BY_VERTEX_LOD0 = 0b10000000000;
    public static final int BONE_USED_BY_VERTEX_LOD1 = 0b100000000000;
    public static final int BONE_USED_BY_VERTEX_LOD2 = 0b1000000000000;
    public static final int BONE_USED_BY_VERTEX_LOD3 = 0b10000000000000;
    public static final int BONE_USED_BY_VERTEX_LOD4 = 0b100000000000000;
    public static final int BONE_USED_BY_VERTEX_LOD5 = 0b1000000000000000;
    public static final int BONE_USED_BY_VERTEX_LOD6 = 0b10000000000000000;
    public static final int BONE_USED_BY_VERTEX_LOD7 = 0b100000000000000000;
    public static final int BONE_USED_BY_BONE_MERGE = 0b1000000000000000000;

    public static final int BONE_TYPE_MASK = 0b111100000000000000000000;
    public static final int BONE_FIXED_ALIGNMENT = 1048576;
    public static final int BONE_HAS_SAVEFRAME_POS = 0b1000000000000000000000;
    public static final int BONE_HAS_SAVEFRAME_ROT = 0b10000000000000000000000;

    public static final int STUDIO_PROC_AXISINTERP = 1;
    public static final int STUDIO_PROC_QUATINTERP = 2;
    public static final int STUDIO_PROC_AIMATBONE = 3;
    public static final int STUDIO_PROC_ANIMATTACH = 4;
    public static final int STUDIO_PROC_JIGGLE = 5;

    public static final int sse = 0b1000000000;

    public int sznameindex;
    public int parent;
    public int[] bonecontroller; //bone controller index, -1 == none

    public Vector3fX pos;
    public QuaternionfX quat;
    public RadianEuler rot;
    public Vector3fX posscale;
    public Vector3fX rotscale;

    public Matrix4f poseToBone;
    public QuaternionfX qAlignment;

    public int flags;
    public int proctype;
    public int procindex; //'procedural rule' - whatever this means.
    public int physicsbone; //index into physically simulated bone

    public int surfacepropidx;
    public int contents;

    public String boneName;

    @Override
    public mstudiobone_t parse(ByteBuffer in) {
        sznameindex = in.getInt();
        boneName = SourceInterop.fetchNullTerminatedString(in, this.structPos + sznameindex, 64);
        //System.out.println("bone name: " + boneName);
        parent = in.getInt();

        bonecontroller = new int[6];
        for (int cIdx = 0; cIdx < 6; cIdx++){
            bonecontroller[cIdx] = in.getInt();
        }

        pos = readVector3fX(in);
        quat = readQuaternionfX(in);

        rot = new RadianEuler().parseStruct(in);

        posscale = readVector3fX(in);
        rotscale = readVector3fX(in);

        poseToBone = new Matrix4f().set(SourceInterop.readMatrix4x3(in));

        qAlignment = readQuaternionfX(in);

        flags = in.getInt();

        proctype = in.getInt();
        procindex = in.getInt();
        physicsbone = in.getInt();

        surfacepropidx = in.getInt();

        contents = in.getInt();

        skip(in, Integer.BYTES * 8); //skip unused[8];

        return this;
    }

    @Override
    public int sizeOf() {
        return 216;
    }
}
