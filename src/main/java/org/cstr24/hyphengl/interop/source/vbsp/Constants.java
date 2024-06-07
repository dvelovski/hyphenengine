package org.cstr24.hyphengl.interop.source.vbsp;

public class Constants {
    public static final int BINARY_BASE_UNDEFINED = -1;

    public static final int VBSP_HEADER = (('P'<<24) + ('S'<<16) + ('B'<<8) + 'V');
    public static final int PSBV_HEADER = (('V'<<24) + ('B'<<16) + ('S'<<8) + 'P');
    public static final int rBSP_HEADER = (('P'<<24) + ('S'<<16) + ('B'<<8) + 'r');
    public static final int HEADER_LUMPS = 64;

    public static final int PLANE_X = 0;
    public static final int PLANE_Y = 1;
    public static final int PLANE_Z = 2;
    public static final int PLANE_ANYX = 3;
    public static final int PLANE_ANYY = 4;
    public static final int PLANE_ANYZ = 5;

    public static final int MAP_MAX_PLANES = 65536;
    public static final int MAP_MAX_VERTS = 65536;
    public static final int MAX_MAP_NODES = 65536;
    public static final int MAP_MAX_EDGES = 256000;

    public static final int MAX_LIGHTMAPS = 4;

    public static final int MIN_MAP_DISP_POWER = 2;
    public static final int MAX_MAP_DISP_POWER = 4;
    public static final int MAX_DISP_CORNER_NEIGHBORS = 4;
}
