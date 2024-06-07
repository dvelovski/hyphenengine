package org.cstr24.hyphengl.interop.source;

import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.vbsp.structs.lump_t;
import org.joml.QuaternionfX;
import org.joml.Vector3fX;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public interface StructWrapper<T extends BaseStruct>{

    /** DO NOT CALL PARSE DIRECTLY - use parseStruct, as parseStruct ensures that the structOffset member is set **/
    T parse(ByteBuffer in);
    default T parseStruct(ByteBuffer in){
        ((BaseStruct) this).setStructPos(in.position());
        //System.out.println("Struct offset: " + ((BaseStruct) this).structOffset);
        return parse(in);
    }
    default T parseStruct(ByteBuffer in, int offset){
        in.position(offset);
        return parseStruct(in);
    }
    int sizeOf();
    default void skip(ByteBuffer in, int numBytes){ //i.e. when we want to ignore padding
        in.position(in.position() + numBytes);
    }
    default int uShortToInt(short in){
        return (in & 0xFFFF);
    }
    default long uIntToLong(int in){
        return (in & 0xffffffffL);
    }
    //in Source they have typedef'd 'unsigned char' to 'byte' therefore
    default short uByteToShort(byte in){
        return (short) (in & 255);
    }
    default short uCharToShort(byte in){
        return uByteToShort(in);
    }
    default float halfPrecisionToFloat(short halfPrecision){
        /* https://stackoverflow.com/a/50889284 */
        int mantissa = halfPrecision & 0x03ff;
        int exponent = halfPrecision & 0x7c00;

        if (exponent == 0x7c00){
            exponent = 0x3fc00;
        }else if (exponent != 0){
            exponent += 0x1c000;

            if (mantissa == 0 && exponent > 0x1c400){
                return Float.intBitsToFloat(
                    (halfPrecision & 0x8000) << 16 | exponent << 13 | 0x3ff
                );
            }
        }else if (mantissa != 0){
            exponent = 0x1c400;

            do {
                mantissa <<= 1;
                exponent -= 0x400;
            } while ((mantissa & 0x400) == 0);

            mantissa &= 0x3ff;
        }
        return Float.intBitsToFloat(
            (halfPrecision & 0x8000) << 16 | (exponent | mantissa) << 13
        );
    }
    default short[] uByteArray(ByteBuffer in, short[] dest){
        for (int i = 0; i < dest.length; i++){
            dest[i] = uByteToShort(in.get());
        }
        return dest;
    }
    default int[] uShortArray(ByteBuffer in, int[] dest){
        for (int i = 0; i < dest.length; i++){
            dest[i] = uShortToInt(in.getShort());
        }
        return dest;
    }
    default int[] intArray(ByteBuffer in, int[] dest){
        for (int i = 0; i < dest.length; i++){
            dest[i] = in.getInt();
        }
        return dest;
    }
    default float[] floatArray(ByteBuffer in, float[] dest){
        for (int i = 0; i < dest.length; i++){
            dest[i] = in.getFloat();
        }
        return dest;
    }
    default String nullTerminatedString(ByteBuffer in, int maxLength){
        return SourceInterop.readNullTerminatedString(in, maxLength, false);
    }
    default Vector3fX readVector3fX(ByteBuffer in){
        return new Vector3fX(
            in.getFloat(),
            in.getFloat(),
            in.getFloat()
        );
    }
    default QuaternionfX readQuaternionfX(ByteBuffer in){
        return new QuaternionfX(
            in.getFloat(),
            in.getFloat(),
            in.getFloat(),
            in.getFloat()
        );
    }

    static <S extends StructWrapper<?>> ArrayList<S> deserializeLump(ByteBuffer in, lump_t lump, Class<S> type){
        ArrayList<S> returnList = new ArrayList<>();

        if (lump.fileLen == 0){
            System.out.println("there are no elements in " + lump.lumpID);
            return returnList;
        }

        try {
            Constructor<S> structConstructor = type.getConstructor();
            int lumpEnd = lump.fileOfs + lump.fileLen;
            int structBaseSize = structConstructor.newInstance().sizeOf();

            in.position(lump.fileOfs);

            while (in.position() < lumpEnd) {
                long currPos = in.position();

                S elem = type.getConstructor().newInstance();
                elem.parse(in);

                long endPos = in.position();

                if (endPos - currPos != elem.sizeOf()) {
                    System.out.println(type.getSimpleName() + " -> expected " + elem.sizeOf() + " and read " + (endPos - currPos) + " bytes instead");
                }

                returnList.add(elem);
            }

            if (returnList.size() != (lump.fileLen / structBaseSize)){
                System.out.println("expected to read " + (lump.fileLen / structBaseSize) + " of " + type.getSimpleName() + " but read " + returnList.size() + " instead");
            }else{
                System.out.println("parsed " + returnList.size() + " of " + type.getSimpleName() + " which is what was expected");
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return returnList;
    }


}
