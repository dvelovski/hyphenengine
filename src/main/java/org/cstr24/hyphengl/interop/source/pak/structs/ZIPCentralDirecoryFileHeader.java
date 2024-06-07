package org.cstr24.hyphengl.interop.source.pak.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ZIPCentralDirecoryFileHeader extends BaseStruct implements StructWrapper<ZIPCentralDirecoryFileHeader> {
    public int versionMadeBy;
    public int versionRequiredForExtraction;

    public int bitFlag;

    public int compressionMethod;

    public int lastModificationDate;
    public int lastModificationTime;

    public long uncompressedCRC32;

    public long compressedSize;
    public long uncompressedSize;

    public String fileName;
    public byte[] fileExtraFieldData;
    public String fileComment;

    public int fileStartDiskNum;

    public int internalFileAttribs;
    public long externalFileAttribs;

    //the number of bytes between the start of the first disk on which the file occurs,
    //and the start of the local file header
    public long localFileHeaderRelativeOffset;

    @Override
    public ZIPCentralDirecoryFileHeader parse(ByteBuffer in) {
        int signature = in.getInt();

        versionMadeBy = uShortToInt(in.getShort());
        versionRequiredForExtraction = uShortToInt(in.getShort());

        bitFlag = uShortToInt(in.getShort());
        compressionMethod = uShortToInt(in.getShort());

        lastModificationTime = uShortToInt(in.getShort());
        lastModificationDate = uShortToInt(in.getShort());

        uncompressedCRC32 = uIntToLong(in.getInt());

        compressedSize = uIntToLong(in.getInt());
        uncompressedSize = uIntToLong(in.getInt());

        int filenameLength = uShortToInt(in.getShort());
        int extraFieldLength = uShortToInt(in.getShort());
        int fileCommentLength = uShortToInt(in.getShort());

        fileStartDiskNum = uShortToInt(in.getShort());

        internalFileAttribs = uShortToInt(in.getShort());
        externalFileAttribs = uIntToLong(in.getInt());

        localFileHeaderRelativeOffset = uIntToLong(in.getInt());

        //System.out.println("\tbytes read: " + (in.position() - pos));
        /*System.out.println("\tfilename length: " + filenameLength +
                " | comment length: " + fileCommentLength +
                " | extra length: " + extraFieldLength +
                " | compressed size: " + compressedSize +
                " | uncompressed size: " + uncompressedSize);*/


        if (filenameLength > 0) {
            var filenameBytes = new byte[filenameLength];
            in.get(filenameBytes);
            fileName = new String(filenameBytes);
        }

        if (extraFieldLength > 0) {
            fileExtraFieldData = new byte[extraFieldLength];
            in.get(fileExtraFieldData);

            /*var extraDataBuffer = ByteBuffer.wrap(fileExtraFieldData).order(ByteOrder.LITTLE_ENDIAN);
            System.out.println("first extra: " + extraDataBuffer.getShort());
            int extraLength = extraDataBuffer.getShort();
            System.out.println("extra length: " + extraLength);

            byte b = extraDataBuffer.get();

            if ((b & (1)) != 0){
                System.out.println("bit 0 set");
                //extraDataBuffer.getLong();
            }
            if ((b & (1 << 1)) != 0){
                System.out.println("bit 1 set");
            }
            if ((b & (1 << 2)) != 0){
                System.out.println("bit 2 set");
            }

            System.out.println("second extra: " + uShortToInt(extraDataBuffer.getShort()));
            System.out.println("second extra length: " + extraDataBuffer.getShort());*/
        }
        if (fileCommentLength > 0) {
            var fileCommentBytes = new byte[fileCommentLength];
            in.get(fileCommentLength);
            fileComment = new String(fileCommentBytes);
        }

        /*System.out.println("\tfilename: " + fileName +
                " | extra: " + Arrays.toString(fileExtraFieldData) +
                " | comment: " + fileComment);

        System.out.println(externalFileAttribs);
        if ((externalFileAttribs & 0x10) != 0) {
            System.out.println("directory located");
        }*/

        return this;
    }

    public boolean isDirectory() {
        return ((externalFileAttribs & 0x10) != 0);
    }

    public boolean isArchive() {
        return ((externalFileAttribs & 0x20) != 0);
    }

    @Override
    public int sizeOf() {
        return 46;
    }
}
