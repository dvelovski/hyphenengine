package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioevent_t extends BaseStruct implements StructWrapper<mstudioevent_t> {
    public static final int SIZE = 80;

    public static final int AE_TYPE_SERVER = 1;
    public static final int AE_TYPE_SCRIPTED = 1 << 1;
    public static final int AE_TYPE_SHARED = 1 << 2;
    public static final int AE_TYPE_WEAPON = 1 << 3;
    public static final int AE_TYPE_CLIENT = 1 << 4;
    public static final int AE_TYPE_FACEPOSER = 1 << 5;
    public static final int AE_TYPE_NEWEVENTSYSTEM = 1 << 10;

    public float cycle;

    public int event;
    public int type;
    public String options;

    public int szeventindex;

    public String eventName;
    public String typeString;

    @Override
    public mstudioevent_t parse(ByteBuffer in) {
        cycle = in.getFloat();

        event = in.getInt();
        type = in.getInt();

        options = SourceInterop.readNullTerminatedString(in, 64, true);

        szeventindex = in.getInt();

        eventName = SourceInterop.fetchNullTerminatedString(in, structPos + szeventindex, 64);

        switch (type){
            case (AE_TYPE_SERVER) -> typeString = "AE_TYPE_SERVER";
            case (AE_TYPE_SCRIPTED) -> typeString = "AE_TYPE_SCRIPTED";
            case (AE_TYPE_SHARED) -> typeString = "AE_TYPE_SHARED";
            case (AE_TYPE_WEAPON) -> typeString = "AE_TYPE_WEAPON";
            case (AE_TYPE_CLIENT) -> typeString = "AE_TYPE_CLIENT";
            case (AE_TYPE_FACEPOSER) -> typeString = "AE_TYPE_FACEPOSER";
            case (AE_TYPE_NEWEVENTSYSTEM) -> typeString = "AE_TYPE_NEWEVENTSYSTEM";
            default -> typeString = "";
        }

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
