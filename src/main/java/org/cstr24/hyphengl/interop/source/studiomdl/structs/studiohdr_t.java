package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class studiohdr_t extends BaseStruct implements StructWrapper<studiohdr_t> {
    public static final int STUDIOHDR_FLAGS_AUTOGENERATED_HITBOX = 0;
    public static final int STUDIOHDR_FLAGS_USES_ENV_CUBEMAP = 1;
    public static final int STUDIOHDR_FLAGS_FORCE_OPAQUE = 2;
    public static final int STUDIOHDR_FLAGS_TRANSLUCENT_TWOPASS = 3;
    public static final int STUDIOHDR_FLAGS_STATIC_PROP = 4;
    public static final int STUDIOHDR_FLAGS_USES_FB_TEXTURE = 5;
    public static final int STUDIOHDR_FLAGS_HASSHADOWLOD = 6;
    public static final int STUDIOHDR_FLAGS_USES_BUMPMAPPING = 7;
    public static final int STUDIOHDR_FLAGS_USE_SHADOWLOD_MATERIALS = 8;
    public static final int STUDIOHDR_FLAGS_OBSOLETE = 9;
    public static final int STUDIOHDR_FLAGS_UNUSED = 10;
    public static final int STUDIOHDR_FLAGS_NO_FORCED_FADE = 11;
    public static final int STUDIOHDR_FLAGS_FORCE_PHONEME_CROSSFADE = 12;
    public static final int STUDIOHDR_FLAGS_CONSTANT_DIRECTIONAL_LIGHT_DOT = 13;
    public static final int STUDIOHDR_FLAGS_FLEXES_CONVERTED = 14;
    public static final int STUDIOHDR_FLAGS_BUILT_IN_PREVIEW_MODE = 15;
    public static final int STUDIOHDR_FLAGS_AMBIENT_BOOST = 16;
    public static final int STUDIOHDR_FLAGS_DO_NOT_CAST_SHADOWS = 17;
    public static final int STUDIOHDR_FLAGS_CAST_TEXTURE_SHADOWS = 18;

    public int id;
    public int version;
    public int checksum;
    public String name = ""; //declared as an array of char - char[64]

    public int length;

    public vector_t eyePosition;
    public vector_t illumPosition;
    public vector_t hull_min;
    public vector_t hull_max;
    public vector_t view_bbmin;
    public vector_t view_bbmax;

    public int flags;

    public int numbones;
    public int boneindex;

    public int numbonecontrollers;
    public int bonecontrollerindex;

    public int numhitboxsets;
    public int hitboxsetindex;

    public int numlocalanim;
    public int localanimindex;

    public int numlocalseq;
    public int localseqindex;
    public int activitylistversion;
    public int eventsindexed;

    public int numtextures;
    public int textureindex;

    public int numcdtextures;
    public int cdtextureindex;

    public int numskinref;
    public int numskinfamilies;
    public int skinindex;

    public int numbodyparts;
    public int bodypartindex;

    public int numlocalattachments;
    public int localattachmentindex;

    public int localnode_count;
    public int localnode_offset;
    public int localnode_name_index;

    public int flexdesc_count;
    public int flexdesc_index;

    public int flexcontroller_count;
    public int flexcontroller_index;

    public int flexrules_count;
    public int flexrules_index;

    public int ikchain_count;
    public int ikchain_index;

    public int mouths_count;
    public int mouths_index;

    public int numlocalposeparameters;
    public int localposeparamindex;

    public int surfaceprop_index;

    public int keyvalue_index;
    public int keyvalue_count;

    public int iklock_count;
    public int iklock_index;

    public float mass;

    public int contents;

    public int includemodel_count;
    public int includemodel_index;


    public int szanimblocknameindex;
    public int numanimblocks;
    public int animblockindex;

    public int animBlockModel;

    public int bonetablename_index;


    public byte directionaldotproduct;
    public byte rootlod;
    public byte numallowedrootlods;

    public int flexcontrollerui_count;
    public int flexcontrollerui_index;

    public float vertanimfixedpointscale;

    public int studiohdr2index;


    @Override
    public studiohdr_t parse(ByteBuffer in) {
        id = in.getInt();
        version = in.getInt();
        checksum = in.getInt();

        name = SourceInterop.readNullTerminatedString(in, 64, true);

        length = in.getInt();

        eyePosition = new vector_t().parse(in);
        illumPosition = new vector_t().parse(in);
        hull_min = new vector_t().parse(in);
        hull_max = new vector_t().parse(in);
        view_bbmin = new vector_t().parse(in);
        view_bbmax = new vector_t().parse(in);

        flags = in.getInt();

        numbones = in.getInt();
        boneindex = in.getInt();

        numbonecontrollers = in.getInt();
        bonecontrollerindex = in.getInt();

        numhitboxsets = in.getInt();
        hitboxsetindex = in.getInt();

        numlocalanim = in.getInt();
        localanimindex = in.getInt();

        numlocalseq = in.getInt();
        localseqindex = in.getInt();

        activitylistversion = in.getInt();
        eventsindexed = in.getInt();

        numtextures = in.getInt();
        textureindex = in.getInt();

        numcdtextures = in.getInt();
        cdtextureindex = in.getInt();

        numskinref = in.getInt();
        numskinfamilies = in.getInt();
        skinindex = in.getInt();

        numbodyparts = in.getInt();
        bodypartindex = in.getInt();

        numlocalattachments = in.getInt();
        localattachmentindex = in.getInt();

        localnode_count = in.getInt();
        localnode_offset = in.getInt();
        localnode_name_index = in.getInt();

        flexdesc_count = in.getInt();
        flexdesc_index = in.getInt();

        flexcontroller_count = in.getInt();
        flexcontroller_index = in.getInt();

        flexrules_count = in.getInt();
        flexrules_index = in.getInt();

        ikchain_count = in.getInt();
        ikchain_index = in.getInt();

        mouths_count = in.getInt();
        mouths_index = in.getInt();

        numlocalposeparameters = in.getInt();
        localposeparamindex = in.getInt();

        surfaceprop_index = in.getInt();

        keyvalue_index = in.getInt();
        keyvalue_count = in.getInt();

        iklock_count = in.getInt();
        iklock_index = in.getInt();

        mass = in.getFloat();

        contents = in.getInt();

        includemodel_count = in.getInt();
        includemodel_index = in.getInt();

        skip(in, 4); //virtualModel, which is a mutable void pointer

        szanimblocknameindex = in.getInt();
        numanimblocks = in.getInt();
        animblockindex = in.getInt();

        animBlockModel = in.getInt();
        //skip(in, 4); //animBlockModel, which is a mutable void pointer. TURNS OUT i actually need it :D

        bonetablename_index = in.getInt();

        skip(in, 8); //pVertexBase, pIndexBase

        directionaldotproduct = in.get();
        rootlod = in.get();
        numallowedrootlods = in.get();

        skip(in, 5); //byte unused[1], int unused4

        flexcontrollerui_count = in.getInt();
        flexcontrollerui_index = in.getInt();

        vertanimfixedpointscale = in.getFloat();

        skip(in, 4); //unused3[1]

        studiohdr2index = in.getInt();

        skip(in, 4); //unused2[1]

        return this;
    }

    @Override
    public int sizeOf() {
        return 408;
    }
}