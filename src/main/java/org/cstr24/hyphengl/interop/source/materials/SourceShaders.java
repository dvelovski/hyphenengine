package org.cstr24.hyphengl.interop.source.materials;

import org.cstr24.hyphengl.rendering.shader.GLShader;
import org.cstr24.hyphengl.rendering.shader.ShaderManager;
import org.cstr24.hyphengl.rendering.shader.ShaderType;
import org.cstr24.hyphengl.rendering.surfaces.MaterialManager;

public class SourceShaders {
    public static void init(){
        var shdLightmappedGeneric = new GLShader();
        shdLightmappedGeneric.setName("LightmappedGeneric");

        shdLightmappedGeneric.shaderFromSource(ShaderType.Vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec3 aNormal;
                    layout (location = 2) in vec2 aUV0;
                    layout (location = 3) in vec2 aUV1;
                    
                    uniform mat4 model;
                    uniform mat4 view;
                    uniform mat4 projection;
                    
                    out vec2 oUV0;
                    out vec2 oUV1;
                    
                    void main(){
                       gl_Position = projection * view * model * vec4(aPos, 1.0);
                       oUV0 = aUV0;
                       oUV1 = aUV1;
                    }
                    """);
        shdLightmappedGeneric.shaderFromSource(ShaderType.Fragment, """
                    #version 330 core
                    uniform sampler2D baseTexture;
                    
                    in vec2 oUV0;
                    in vec2 oUV1;
                    
                    out vec4 FragColor;
                                        
                    void main(){
                        FragColor = texture(baseTexture, oUV0);
                    }
                    """);
        shdLightmappedGeneric.link();
        ShaderManager.get().addShader(shdLightmappedGeneric);

        //LightmappedGeneric Material
        var matLightmappedGeneric = new LightmappedGenericMaterial();
        matLightmappedGeneric.setShader(shdLightmappedGeneric);
        MaterialManager.get().addMaterial(matLightmappedGeneric);

        var shdUnlitGeneric = new GLShader();
        shdUnlitGeneric.setName("UnlitGeneric");

        shdUnlitGeneric.shaderFromSource(ShaderType.Vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec3 aNormal;
                    layout (location = 2) in vec2 aUV0;
                    layout (location = 3) in vec2 aUV1;
                    
                    uniform mat4 model;
                    uniform mat4 view;
                    uniform mat4 projection;
                    
                    out vec2 oUV0;
                    out vec2 oUV1;
                    
                    void main(){
                       gl_Position = projection * view * model * vec4(aPos, 1.0);
                       oUV0 = aUV0;
                       oUV1 = aUV1;
                    }
                    """);
        shdUnlitGeneric.shaderFromSource(ShaderType.Fragment, """
                    #version 330 core
                    uniform sampler2D baseTexture;
                    
                    in vec2 oUV0;
                    in vec2 oUV1;
                    
                    out vec4 FragColor;
                                        
                    void main(){
                        FragColor = texture(baseTexture, oUV0);
                    }
                    """);
        shdUnlitGeneric.link();
        ShaderManager.get().addShader(shdUnlitGeneric);

        //LightmappedGeneric Material
        var matUnlitGeneric = new UnlitGenericMaterial();
        matUnlitGeneric.setShader(shdUnlitGeneric);
        MaterialManager.get().addMaterial(matUnlitGeneric);

        //WorldVertexTransition shader
        var shdWorldVertexTransition = new GLShader();
        shdWorldVertexTransition.setName("WorldVertexTransition");

        shdWorldVertexTransition.shaderFromSource(ShaderType.Vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec3 aNormal;
                    layout (location = 2) in vec2 aUV0;
                    layout (location = 3) in vec2 aUV1;
                    
                    uniform mat4 model;
                    uniform mat4 view;
                    uniform mat4 projection;
                    
                    out vec2 oUV0;
                    out vec2 oUV1;
                    
                    void main(){
                       gl_Position = projection * view * model * vec4(aPos, 1.0);
                       oUV0 = aUV0;
                       oUV1 = aUV1;
                    }
                    """);
        shdWorldVertexTransition.shaderFromSource(ShaderType.Fragment, """
                    #version 330 core
                    uniform sampler2D baseTexture;
                    uniform sampler2D baseTexture2;
                    
                    in vec2 oUV0;
                    in vec2 oUV1;
                    
                    out vec4 FragColor;
                                        
                    void main(){                        
                        vec4 t0 = texture2D(baseTexture, oUV0);
                        vec4 t1 = texture2D(baseTexture2, oUV0);
                        
                        FragColor = mix(t0, t1, 0.1);
                    }
                    """);
        shdWorldVertexTransition.link();
        ShaderManager.get().addShader(shdWorldVertexTransition);

        //WorldVertexTransition material
        var matWorldVertexTransition = new WorldVertexTransitionMaterial();
        matWorldVertexTransition.setShader(shdWorldVertexTransition);
        MaterialManager.get().addMaterial(matWorldVertexTransition);

        //VertexLitGeneric shader
        var shdVertexLitGeneric = new GLShader();
        shdVertexLitGeneric.setName("VertexLitGeneric");

        shdVertexLitGeneric.shaderFromSource(ShaderType.Vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec3 aNormal;
                    layout (location = 2) in vec2 aUV0;
                    layout (location = 3) in vec4 aTangent;
                    layout (location = 4) in vec3 aBoneWeights;
                    layout (location = 5) in ivec4 aBoneIndices;
                    
                    const int MAX_BONES = 128;
                    
                    uniform mat4 model;
                    uniform mat4 view;
                    uniform mat4 projection;
                    
                    uniform mat4 boneMatrices[MAX_BONES];
                    
                    out vec2 oUV0;
                    
                    void main(){
                        if (aBoneIndices[3] > 0){
                            mat4 boneTransform;
                    
                            for (int i = 0; i < aBoneIndices[3]; i++){
                                mat4 tI = boneMatrices[aBoneIndices[i]];
                                boneTransform += tI * aBoneWeights[i];
                            }
                            gl_Position = projection * view * model * (boneTransform * vec4(aPos, 1.0f));
                        }else{
                            gl_Position = projection * view * model * vec4(aPos, 1.0f);
                        }
                    
                        oUV0 = aUV0;
                    }
                    """);
        shdVertexLitGeneric.shaderFromSource(ShaderType.Fragment, """
                    #version 330 core
                    uniform sampler2D baseTexture;
                    uniform sampler2D lightwarpTexture;
                    
                    in vec2 oUV0;
                    
                    out vec4 FragColor;
                    
                    void main(){
                        vec4 t0 = texture2D(baseTexture, oUV0);
                    
                        float luma = 0.2126 * t0.r + 0.7152 * t0.g + 0.0722 * t0.b;
                    
                        vec4 t1 = texture2D(lightwarpTexture, vec2(luma, 0.0));
                    
                        FragColor = t0;
                    }
                    """);
        shdVertexLitGeneric.link();
        ShaderManager.get().addShader(shdVertexLitGeneric);

        //VertexLitGeneric material
        var matVertexLitGeneric = new VertexLitGenericMaterial();
        matVertexLitGeneric.setShader(shdVertexLitGeneric);
        MaterialManager.get().addMaterial(matVertexLitGeneric);

        //Water shader
        var shdWater = new GLShader();
        shdWater.setName("Water");

        shdWater.shaderFromSource(ShaderType.Vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec3 aNormal;
                    
                    uniform mat4 model;
                    uniform mat4 view;
                    uniform mat4 projection;
                    
                    void main(){
                       gl_Position = projection * view * model * vec4(aPos, 1.0);
                    }
                    """);
        shdWater.shaderFromSource(ShaderType.Fragment, """
                    #version 330 core
                                        
                    out vec4 FragColor;
                                        
                    void main(){
                        FragColor = vec4(0, 0, 1, 1);
                    }
                    """);
        shdWater.link();
        ShaderManager.get().addShader(shdWater);

        var matWater = new WaterMaterial();
        matWater.setShader(shdWater);
        MaterialManager.get().addMaterial(matWater);

        //EyeRefract shader
        var shdEyeRefract = new GLShader();
        shdEyeRefract.setName("EyeRefract");

        shdEyeRefract.shaderFromSource(ShaderType.Vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec3 aNormal;
                    layout (location = 2) in vec2 aUV0;
                    layout (location = 3) in vec4 aTangent;
                    layout (location = 4) in vec3 aBoneWeights;
                    layout (location = 5) in ivec4 aBoneIndices;
                    
                    const int MAX_BONES = 128;                  
                    
                    uniform mat4 model;
                    uniform mat4 view;
                    uniform mat4 projection;
                    
                    uniform mat4 boneMatrices[MAX_BONES];
                    
                    out vec2 oUV0;
                    
                    void main(){
                        if (aBoneIndices[3] > 0){
                            mat4 boneTransform;
                    
                            for (int i = 0; i < aBoneIndices[3]; i++){
                                mat4 tI = boneMatrices[aBoneIndices[i]];
                                boneTransform += tI * aBoneWeights[i];
                            }
                            gl_Position = projection * view * model * (boneTransform * vec4(aPos, 1.0f));
                        }else{
                            gl_Position = projection * view * model * vec4(aPos, 1.0f);
                        }
                    
                        oUV0 = aUV0;
                    }
                    """);
        shdEyeRefract.shaderFromSource(ShaderType.Fragment, """
                    #version 330 core
                    uniform sampler2D iris;
                    uniform sampler2D corneaTexture;
                    
                    in vec2 oUV0;
                    
                    out vec4 FragColor;
                    
                    void main(){
                        vec4 t0 = texture2D(iris, oUV0);
                        vec4 t1 = texture2D(corneaTexture, oUV0);
                        
                        FragColor = mix(t0, t1, 0.0);
                    }
                    """);
        shdEyeRefract.link();
        ShaderManager.get().addShader(shdEyeRefract);

        //EyeRefract material
        var matEyeRefract = new EyeRefractMaterial();
        matEyeRefract.setShader(shdEyeRefract);
        MaterialManager.get().addMaterial(matEyeRefract);

        //UnLitTwoTexture shader
        var shdUnLitTwoTex = new GLShader();
        shdUnLitTwoTex.setName("UnLitTwoTexture");

        shdUnLitTwoTex.shaderFromSource(ShaderType.Vertex, """
                    #version 330 core
                    layout (location = 0) in vec3 aPos;
                    layout (location = 1) in vec3 aNormal;
                    layout (location = 2) in vec2 aUV0;
                    layout (location = 3) in vec4 aTangent;
                    layout (location = 4) in vec3 aBoneWeights;
                    layout (location = 5) in ivec4 aBoneIndices;
                    
                    const int MAX_BONES = 128;
                    
                    uniform mat4 model;
                    uniform mat4 view;
                    uniform mat4 projection;
                    
                    uniform mat4 boneMatrices[MAX_BONES];
                    
                    out vec2 oUV0;
                    
                    void main(){
                        if (aBoneIndices[3] > 0){
                            mat4 boneTransform;
                    
                            for (int i = 0; i < aBoneIndices[3]; i++){
                                mat4 tI = boneMatrices[aBoneIndices[i]];
                                boneTransform += tI * aBoneWeights[i];
                            }
                            gl_Position = projection * view * model * (boneTransform * vec4(aPos, 1.0f));
                        }else{
                            gl_Position = projection * view * model * vec4(aPos, 1.0f);
                        }
                    
                        oUV0 = aUV0;
                    }
                    """);
        shdUnLitTwoTex.shaderFromSource(ShaderType.Fragment, """
                    #version 330 core
                    uniform sampler2D baseTexture;
                    uniform sampler2D texture2;
                    
                    in vec2 oUV0;
                    
                    out vec4 FragColor;
                    
                    void main(){
                        vec4 t0 = texture2D(baseTexture, oUV0);
                        vec4 t1 = texture2D(texture2, oUV0);

                        FragColor = t0 * t1;
                    }
                    """);
        shdUnLitTwoTex.link();
        ShaderManager.get().addShader(shdUnLitTwoTex);

        //VertexLitGeneric material
        var matUnLitTwoTex = new UnLitTwoTextureMaterial();
        matUnLitTwoTex.setShader(shdUnLitTwoTex);
        MaterialManager.get().addMaterial(matUnLitTwoTex);
    }
}
