package org.cstr24.hyphenengine.rendering.shader;

import org.cstr24.hyphenengine.data.ArrayElementDescriptor;
import org.cstr24.hyphenengine.data.ComponentType;
import org.cstr24.hyphenengine.data.ElementDescriptor;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class GLShader extends HyShader {
    private final HashMap<Integer, Integer> programMap; //type, program ID
    private final HashMap<Integer, GLShaderProgram> shaderPrograms; //program ID, description object

    public GLShader(){
        libCreate();
        programMap = new HashMap<>();
        shaderPrograms = new HashMap<>();

        shaderUniforms = new HashMap<>();
    }
    void libCreate(){
        this.shaderID = GL20.glCreateProgram();
    }

    static final ElementDescriptor glBool = new ElementDescriptor(ComponentType.Boolean);
    static final ElementDescriptor glByte = new ElementDescriptor(ComponentType.Byte);
    static final ElementDescriptor glUByte = new ElementDescriptor(ComponentType.UnsignedByte);
    static final ElementDescriptor glShort = new ElementDescriptor(ComponentType.Short);
    static final ElementDescriptor glUShort = new ElementDescriptor(ComponentType.UnsignedShort);
    static final ElementDescriptor glInt = new ElementDescriptor(ComponentType.Int);
    static final ElementDescriptor glUInt = new ElementDescriptor(ComponentType.UnsignedInt);
    static final ElementDescriptor glFloat = new ElementDescriptor(ComponentType.Float);
    static final ElementDescriptor gl2Bytes = new ElementDescriptor(ComponentType.Byte, 2);
    static final ElementDescriptor gl3Bytes = new ElementDescriptor(ComponentType.Byte, 3);
    static final ElementDescriptor gl4Bytes = new ElementDescriptor(ComponentType.Byte, 4);
    static final ElementDescriptor glDouble = new ElementDescriptor(ComponentType.Double);

    static final ElementDescriptor glTexture1D = new ElementDescriptor().ofTexture(1);
    static final ElementDescriptor glTexture2D = new ElementDescriptor().ofTexture(2);
    static final ElementDescriptor glTexture3D = new ElementDescriptor().ofTexture(3);
    static final ElementDescriptor glTextureCubemap = new ElementDescriptor().ofTexture(6);

    static final ElementDescriptor glFloatVec2 = new ElementDescriptor(ComponentType.Float).ofVector(2);
    static final ElementDescriptor glFloatVec3 = new ElementDescriptor(ComponentType.Float).ofVector(3);
    static final ElementDescriptor glFloatVec4 = new ElementDescriptor(ComponentType.Float).ofVector(4);

    static final ElementDescriptor glIntVec2 = new ElementDescriptor(ComponentType.Int).ofVector(2);
    static final ElementDescriptor glIntVec3 = new ElementDescriptor(ComponentType.Int).ofVector(3);
    static final ElementDescriptor glIntVec4 = new ElementDescriptor(ComponentType.Int).ofVector(4);

    static final ElementDescriptor glBoolVec2 = new ElementDescriptor(ComponentType.Boolean).ofVector(2);
    static final ElementDescriptor glBoolVec3 = new ElementDescriptor(ComponentType.Boolean).ofVector(3);
    static final ElementDescriptor glBoolVec4 = new ElementDescriptor(ComponentType.Boolean).ofVector(4);

    static final ElementDescriptor glFloatMat2 = new ElementDescriptor(ComponentType.Float).of2x2Matrix();
    static final ElementDescriptor glFloatMat3 = new ElementDescriptor(ComponentType.Float).of3x3Matrix();
    static final ElementDescriptor glFloatMat4 = new ElementDescriptor(ComponentType.Float).of4x4Matrix();

    ElementDescriptor enumToType(int typeIn){
        switch (typeIn){
            case GL20.GL_BOOL -> {
                return glBool;
            }
            case GL11.GL_BYTE -> {
                return glByte;
            }
            case GL11.GL_UNSIGNED_BYTE -> {
                return glUByte;
            }
            case GL11.GL_SHORT -> {
                return glShort;
            }
            case GL11.GL_UNSIGNED_SHORT -> {
                return glUShort;
            }
            case GL11.GL_INT -> {
                return glInt;
            }
            case GL11.GL_UNSIGNED_INT -> {
                return glUInt;
            }
            case GL11.GL_FLOAT -> {
                return glFloat;
            }
            case GL11.GL_2_BYTES -> {
                return gl2Bytes;
            }
            case GL11.GL_3_BYTES -> {
                return gl3Bytes;
            }
            case GL11.GL_4_BYTES -> {
                return gl4Bytes;
            }
            case GL11.GL_DOUBLE -> {
                return glDouble;
            }
            case GL20.GL_FLOAT_VEC2 -> {
                return glFloatVec2;
            }
            case GL20.GL_FLOAT_VEC3 -> {
                return glFloatVec3;
            }
            case GL20.GL_FLOAT_VEC4 -> {
                return glFloatVec4;
            }
            case GL20.GL_INT_VEC2 -> {
                return glIntVec2;
            }
            case GL20.GL_INT_VEC3 -> {
                return glIntVec3;
            }
            case GL20.GL_INT_VEC4 -> {
                return glIntVec4;
            }
            case GL20.GL_BOOL_VEC2 -> {
                return glBoolVec2;
            }
            case GL20.GL_BOOL_VEC3 -> {
                return glBoolVec3;
            }
            case GL20.GL_BOOL_VEC4 -> {
                return glBoolVec4;
            }
            case GL20.GL_FLOAT_MAT2 -> {
                return glFloatMat2;
            }
            case GL20.GL_FLOAT_MAT3 -> {
                return glFloatMat3;
            }
            case GL20.GL_FLOAT_MAT4 -> {
                return glFloatMat4;
            }
            case GL20.GL_SAMPLER_1D -> {
                return glTexture1D;
            }
            case GL20.GL_SAMPLER_2D -> {
                return glTexture2D;
            }
            case GL20.GL_SAMPLER_3D -> {
                return glTexture3D;
            }
            case GL20.GL_SAMPLER_CUBE -> {
                return glTextureCubemap;
            }
            default -> {
                System.out.println("Unknown gl enum: " + typeIn + " (hex: " + Integer.toHexString(typeIn) + ")");
                return null;
            }
        }

    }

    int shaderTypeToEnum(ShaderType in){
        switch (in){
            case Vertex -> {
                return GL20.GL_VERTEX_SHADER;
            }
            case TesselationControl -> {
                return GL40.GL_TESS_CONTROL_SHADER;
            }
            case TesselationEvaluation -> {
                return GL40.GL_TESS_EVALUATION_SHADER;
            }
            case Geometry -> {
                return GL32.GL_GEOMETRY_SHADER;
            }
            case Fragment -> {
                return GL20.GL_FRAGMENT_SHADER;
            }
        }
        return 0;
    }

    @Override
    public HyShader shaderFromSource(ShaderType type, String source) {
        int sTypeEnum = shaderTypeToEnum(type);

        if (programMap.containsKey(sTypeEnum)){
            //we need to destroy the existing one
            int existing = programMap.get(sTypeEnum);
            shaderPrograms.remove(existing);
            GL20.glDeleteShader(existing);
        } //refactor to method as 'shaderFromBinary' will use this too

        var shaderProgram = new GLShaderProgram();
        shaderProgram.type = type;
        shaderProgram.format = ShaderFormat.GLSL;
        shaderProgram.binaryFormat = ShaderBinaryFormat.String;

        shaderProgram.ID = GL20.glCreateShader(sTypeEnum);
        GL20.glShaderSource(shaderProgram.ID, source);
        GL20.glCompileShader(shaderProgram.ID);

        programMap.put(sTypeEnum, shaderProgram.ID);
        shaderPrograms.put(shaderProgram.ID, shaderProgram);

        return this;
    }

    @Override
    public HyShader shaderFromBinary(ShaderType type, ByteBuffer binary) {
        return null;
    }

    @Override
    public HyShader link() {
        //this is where we'll go through our 'programMap' and for each value in there call 'attachShader'
        programMap.values().forEach(pID -> {
            GL20.glAttachShader(this.shaderID, pID);
        });
        GL20.glLinkProgram(this.shaderID);

        //now delete them
        programMap.values().forEach(GL20::glDeleteShader);

        introspect();

        return this;
    }

    @Override
    public HyShader introspect() {
        boolean intQuery = GL.getCapabilities().GL_ARB_program_interface_query;
        if (intQuery){

        }else{
            try (MemoryStack stack = MemoryStack.stackPush()){
                int numUniforms = GL41.glGetProgrami(shaderID, GL41.GL_ACTIVE_UNIFORMS);
                System.out.println(" > num uniforms: " + numUniforms);
                for (int index = 0; index < numUniforms; index++){
                    int uBlockIndex = GL41.glGetActiveUniformsi(shaderID, index, GL41.GL_UNIFORM_BLOCK_INDEX);
                    int uType = GL41.glGetActiveUniformsi(shaderID, index, GL41.GL_UNIFORM_TYPE);
                    int uArrayLength = GL41.glGetActiveUniformsi(shaderID, index, GL41.GL_UNIFORM_SIZE);

                    if (uBlockIndex == -1){
                        String uniformName = GL41.glGetActiveUniformName(shaderID, index);
                        ElementDescriptor descriptor;
                        if (uArrayLength > 1){
                            descriptor = new ArrayElementDescriptor().copyFrom(enumToType(index)).withLength(uArrayLength);
                        }else{
                            descriptor = enumToType(uType);
                        }

                        GLShaderUniform theUniform = new GLShaderUniform(shaderID, index, uniformName, descriptor);
                        shaderUniforms.put(uniformName, theUniform);

                        System.out.println(" -> uniform " + index + ": " + uniformName + " - type " + enumToType(uType) + ", location " + index);
                    }
                }
            }
        }
        return this;
    }

    @Override
    public HyShader bind() {
        var sManager = ShaderManager.get();
        GL20.glUseProgram(shaderID);
        if (!sManager.shaderBound(shaderID)){
            sManager.setCurrentShader(shaderID);
        }
        return this;
    }

    @Override
    public void printDetails() {
        System.out.println("--- Shader " + shaderID + " | name: " + shaderName + " ---");
        System.out.println(" ◿ programs (x" + shaderPrograms.size() + ") ---");
        shaderPrograms.values().forEach(prog -> {
            System.out.println("  - program: type " + prog.type + " | ID " + prog.ID + " - format " + prog.format + " provided as " + prog.binaryFormat);
        });

        if (!shaderUniforms.isEmpty()){
            System.out.println(" ◿ uniforms (x" + shaderUniforms.size() + ") ---");
            shaderUniforms.values().forEach(unif -> {
                System.out.println("  - " + unif.toString());
            });
        }
    }

    public static class GLShaderProgram{
        public int ID;
        public ShaderType type;
        public ShaderFormat format;
        public ShaderBinaryFormat binaryFormat;
    }
}
