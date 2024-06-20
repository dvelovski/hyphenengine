package org.cstr24.hyphenengine.utils;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListUtils {
    public static FloatBuffer vec2fArrayListToFloats(ArrayList<Vector2f> in){
        var toReturn = BufferUtils.createFloatBuffer(in.size() * 2);
        in.forEach(v2 -> toReturn.put(v2.x).put(v2.y));
        return toReturn;
    }
    public static FloatBuffer vec3fArrayListToFloats(ArrayList<Vector3f> in){
        var toReturn = BufferUtils.createFloatBuffer(in.size() * 3);
        in.forEach(v3 -> toReturn.put(v3.x).put(v3.y).put(v3.z));
        return toReturn;
    }
    public static IntBuffer intArrayListToIntBuffer(ArrayList<Integer> in){
        var toReturn = BufferUtils.createIntBuffer(in.size());
        in.forEach(toReturn::put);
        return toReturn;
    }
    public static <S> List<S> createAndFill(Supplier<S> supplier, int amount){
        return Stream.generate(supplier).limit(amount).collect(Collectors.toList());
    }

}
