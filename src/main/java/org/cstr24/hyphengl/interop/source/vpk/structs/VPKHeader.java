package org.cstr24.hyphengl.interop.source.vpk.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class VPKHeader extends BaseStruct implements StructWrapper<VPKHeader> {
    public static final int MAGIC = 0x55aa1234;
    public int treeSize;

    //How many bytes of file content are stored in this VPK file (0 in CS:GO)
    public int fileDataSectionSize;
    //the size, in bytes, of the section containing MD5 checksums
    //for external archive content
    public int archiveMD5SectionSize;

    //the size, in bytes, of the section containing MD5 checksums
    //for content in this file
    //should always be 48
    public int otherMD5SectionSize;

    //the size, in bytes, of the section containing the public key and signature
    //this is either 0 (CS:GO and The Ship)
    //or 296 (HL2, HL2:DM, HL2:EP1, HL2:EP2, HL2:LC, TF2, DOD:S and CS:S)
    public int signatureSectionSize;

    @Override
    public VPKHeader parse(ByteBuffer in) {
        treeSize = in.getInt();
        return this;
    }

    @Override
    public int sizeOf() {
        return 12;
    }
}
