package org.cstr24.hyphengl.interop.source.studiomdl;

import org.cstr24.hyphengl.interop.source.structs.vector_t;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.Quaternion;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.RadianEuler;
import org.joml.QuaternionfX;
import org.joml.Vector3f;
import org.joml.Vector3fX;

public class LinearBone {
    int flags;
    int parent;
    Vector3fX pos;
    Vector3fX posScale;
    RadianEuler rot;
    vector_t rotScale;
    QuaternionfX quat;
    QuaternionfX qAlignment;
}
