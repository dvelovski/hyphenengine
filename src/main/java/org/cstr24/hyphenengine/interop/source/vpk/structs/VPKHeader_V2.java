package org.cstr24.hyphenengine.interop.source.vpk.structs;

import java.nio.ByteBuffer;

public class VPKHeader_V2 extends VPKHeader {
    @Override
    public VPKHeader parse(ByteBuffer in) {
        super.parse(in);

        fileDataSectionSize = in.getInt();
        archiveMD5SectionSize = in.getInt();
        otherMD5SectionSize = in.getInt();
        signatureSectionSize = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return 28;
    }
}
