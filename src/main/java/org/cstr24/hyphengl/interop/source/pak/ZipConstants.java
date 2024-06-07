package org.cstr24.hyphengl.interop.source.pak;

import static org.cstr24.hyphengl.interop.source.SourceInterop.*;

public class ZipConstants {
    public static final int CENTRAL_DIRECTORY_ENTRY_SIGNATURE = 0x02014b50;

    public static final int END_OF_CENTRAL_DIRECTORY_SIGNATURE = 0x06054b50;
    public static final byte[] EOCD_SIGNATURE_BYTES = intToByteArray(
        END_OF_CENTRAL_DIRECTORY_SIGNATURE
    );

    /* external file attribute constants */
    public static final int FILE_ATTRIBUTE_READONLY = 0x1;
    public static final int FILE_ATTRIBUTE_HIDDEN = 0x2;
    public static final int FILE_ATTRIBUTE_SYSTEM = 0x4;
    public static final int FILE_ATTRIBUTE_DIRECTORY = 0x10;
    public static final int FILE_ATTRIBUTE_ARCHIVE = 0x20;
    public static final int FILE_ATTRIBUTE_DEVICE = 0x40;
    public static final int FILE_ATTRIBUTE_NORMAL = 0x80;
    public static final int FILE_ATTRIBUTE_TEMPORARY = 0x100;
    public static final int FILE_ATTRIBUTE_SPARSE_FILE = 0x200;
    public static final int FILE_ATTRIBUTE_REPARSE_POINT = 0x400;
    public static final int FILE_ATTRIBUTE_COMPRESSED = 0x800;
    public static final int FILE_ATTRIBUTE_OFFLINE = 0x1000;
    public static final int FILE_ATTRIBUTE_NOT_CONTENT_INDEXED = 0x2000;
    public static final int FILE_ATTRIBUTE_ENCRYPTED = 0x4000;
    public static final int FILE_ATTRIBUTE_INTEGRITY_STREAM = 0x8000;
    public static final int FILE_ATTRIBUTE_VIRTUAL = 0x10000;
    public static final int FILE_ATTRIBUTE_NO_SCRUB_DATA = 0x20000;
    public static final int FILE_ATTRIBUTE_RECALL_ON_OPEN = 0x40000;
    public static final int FILE_ATTRIBUTE_RECALL_ON_DATA_ACCESS = 0x400000;

    /* version made by mappings */
    public static final int MADE_BY_MSDOS = 0; //FAT / VFAT / FAT32 FS
    public static final int MADE_BY_AMIGA = 1;
    public static final int MADE_BY_OPENVMS = 2;
    public static final int MADE_BY_UNIX = 3;
    public static final int MADE_BY_VM_CMS = 4;
    public static final int MADE_BY_VM_ATARI_ST = 5;
    public static final int MADE_BY_OS2_HPFS = 6;
    public static final int MADE_BY_MACINTOSH = 7;
    public static final int MADE_BY_Z_SYSTEM = 8;
    public static final int MADE_BY_CPM = 9;
    public static final int MADE_BY_NTFS = 10; //Windows NTFS
    public static final int MADE_BY_MVS = 11; //OS/390 - Z/0S
    public static final int MADE_BY_VSE = 12;
    public static final int MADE_BY_ACORN_RISC = 13;
    public static final int MADE_BY_VFAT = 14;
    public static final int MADE_BY_ALTERNATE_MVS = 15;
    public static final int MADE_BY_BEOS = 16;
    public static final int MADE_BY_TANDEM = 17;
    public static final int MADE_BY_OS400 = 18;
    public static final int MADE_BY_OSX = 19;
    public static String versionMadeToString(int constant){
        switch (constant){
            case MADE_BY_MSDOS -> {return "MS-DOS or OS/2";}
            case MADE_BY_AMIGA -> {return "Amiga";}
            case MADE_BY_OPENVMS -> {return "OpenVMS";}
            case MADE_BY_UNIX -> {return "UNIX";}
            case MADE_BY_VM_CMS -> {return "VM/CMS";}
            case MADE_BY_VM_ATARI_ST -> {return "Atari ST";}
            case MADE_BY_OS2_HPFS -> {return "OS/2 H.P.F.S.";}
            case MADE_BY_MACINTOSH -> {return "Macintosh";}
            case MADE_BY_Z_SYSTEM -> {return "Z-System";}
            case MADE_BY_CPM -> {return "CP/M";}
            case MADE_BY_NTFS ->  {return "Windows NTFS";}
            case MADE_BY_MVS -> {return "MVS (OS/390 - Z/OS)";}
            case MADE_BY_VSE -> {return "VSE";}
            case MADE_BY_ACORN_RISC -> {return "Acorn Risc";}
            case MADE_BY_VFAT -> {return "VFAT";}
            case MADE_BY_ALTERNATE_MVS -> {return "alternate MVS";}
            case MADE_BY_BEOS -> {return "BeOS";}
            case MADE_BY_TANDEM -> {return "Tandem";}
            case MADE_BY_OS400 -> {return "OS/400";}
            case MADE_BY_OSX -> {return "OS X (Darwin)";}
            default -> {return "Undefined";}
        }
    }

    /* compression method mappings */
    public static final int METHOD_STORED = 0;
    public static final int METHOD_SHRUNK = 1;
    public static final int METHOD_REDUCED_FACTOR1 = 2;
    public static final int METHOD_REDUCED_FACTOR2 = 3;
    public static final int METHOD_REDUCED_FACTOR3 = 4;
    public static final int METHOD_REDUCED_FACTOR4 = 5;
    public static final int METHOD_IMPLODED = 6;
    public static final int METHOD_RESERVED0 = 7; //reserved for "tokenizing compression algorithm"
    public static final int METHOD_DEFLATED = 8;
    public static final int METHOD_DEFLATE64 = 9; //enhanced deflating using Deflate64(tm)
    public static final int METHOD_PKWARE_IMPLODE = 10; //PKWARE Data Compression Library Impliding (old IBM TERSE)
    public static final int METHOD_RESERVED1 = 11; //reserved by PKWARE
    public static final int METHOD_BZIP2 = 12;
    public static final int METHOD_RESERVED2 = 13; //reserved by PKWARE
    public static final int METHOD_LZMA = 14;
    public static final int METHOD_RESERVED3 = 15; //reserved by PKWARE
    public static final int METHOD_IBM_ZOS = 16; //IBM z/OS CMPSC Compression
    public static final int METHOD_RESERVED4 = 17; //reserved by PKWARE
    public static final int METHOD_IBM_TERSE = 18;
    public static final int METHOD_IBM_LZ77 = 19;
    public static final int METHOD_DEPRECATED0 = 20; //deprecated
    public static final int METHOD_ZSTANDARD = 93;
    public static final int METHOD_MP3 = 94;
    public static final int METHOD_XZ = 95;
    public static final int METHOD_JPEG = 96;
    public static final int METHOD_WAVPACK = 97;
    public static final int METHOD_PPMD_VIR1 = 98; //PPMd version I, Rev 1
    public static final int METHOD_AEX = 99; //AE-x encryption marker
    public static String compressionMethodToString(int constant){
        switch (constant){
            case METHOD_STORED -> {return "Stored (no compression)";}
            case METHOD_SHRUNK -> {return "Shrunk";}
            case METHOD_REDUCED_FACTOR1 -> {return "Reduced with compression factor 1";}
            case METHOD_REDUCED_FACTOR2 -> {return "Reduced with compression factor 2";}
            case METHOD_REDUCED_FACTOR3 -> {return "Reduced with compression factor 3";}
            case METHOD_REDUCED_FACTOR4 -> {return "Reduced with compression factor 4";}
            case METHOD_IMPLODED ->  {return "Imploded";}
            case METHOD_RESERVED0 -> {return "Reserved0 - Tokenizing compression algorithm";}
            case METHOD_DEFLATED -> {return "Deflated";}
            case METHOD_DEFLATE64 -> {return "Enhanced Deflation with Deflate64(tm)";}
            case METHOD_PKWARE_IMPLODE -> {return "PKWARE Data Compression Library Implosion";}
            case METHOD_RESERVED1 -> {return "Reserved1 - Reserved by PKWARE";}
            case METHOD_BZIP2 -> {return "BZIP2";}
            case METHOD_RESERVED2 -> {return "Reserved2 - Reserved by PKWARE";}
            case METHOD_LZMA -> {return "LZMA";}
            case METHOD_RESERVED3 -> {return "Reserved3 - Reserved by PKWARE";}
            case METHOD_IBM_ZOS -> {return "IBM z/OS CMPSC Compression";}
            case METHOD_RESERVED4 -> {return "Reserved4 - Reserved by PKWARE";}
            case METHOD_IBM_TERSE -> {return "IBM TERSE (new)";}
            case METHOD_IBM_LZ77 -> {return "IBM LZ77 z Architecture";}
            case METHOD_DEPRECATED0 -> {return "Deprecated0";}
            case METHOD_ZSTANDARD -> {return "ZStandard (zstd)";}
            case METHOD_MP3 -> {return "MP3 Compression";}
            case METHOD_XZ -> {return "XZ Cmpression";}
            case METHOD_JPEG -> {return "JPEG variant";}
            case METHOD_WAVPACK -> {return "WavPack compressed data";}
            case METHOD_PPMD_VIR1 -> {return "PPMd version I, Rev 1";}
            case METHOD_AEX -> {return "AE-x encryption";}
            default -> {return "Unknown";}
        }
    }
}
