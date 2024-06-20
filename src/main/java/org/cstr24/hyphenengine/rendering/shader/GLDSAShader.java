package org.cstr24.hyphenengine.rendering.shader;

import org.cstr24.hyphenengine.data.ArrayElementDescriptor;
import org.cstr24.hyphenengine.data.ElementDescriptor;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

public class GLDSAShader extends GLShader{
    @Override
    public HyShader introspect() {
        //ShaderUniform construction
        try (MemoryStack stack = MemoryStack.stackPush()){
            var numUniforms = stack.mallocInt(1);
            var values = stack.mallocInt(4);
            var properties = stack.ints(GL45.GL_BLOCK_INDEX, GL45.GL_TYPE, GL45.GL_LOCATION, GL45.GL_ARRAY_SIZE);

            GL45.glGetProgramInterfaceiv(shaderID, GL45.GL_UNIFORM, GL45.GL_ACTIVE_RESOURCES, numUniforms);
            System.out.println(" > num uniforms: " + numUniforms.get(0));
            for (int u = 0; u < numUniforms.get(0); u++){
                GL45.glGetProgramResourceiv(shaderID, GL45.GL_UNIFORM, u, properties, null, values);

                int uBlockIndex = values.get(0);
                int uType = values.get(1);
                int uLocation = values.get(2);
                int uArrayLength = values.get(3);

                if (uBlockIndex == -1){
                    var uniformName = GL45.glGetProgramResourceName(shaderID, GL45.GL_UNIFORM, u);
                    ElementDescriptor componentType;
                    if (uArrayLength > 1){
                        System.out.println(" -> uniform is an array with length " + uArrayLength);
                        componentType = new ArrayElementDescriptor().copyFrom(enumToType(uType)).withLength(uArrayLength);
                    }else{
                        componentType = enumToType(uType);
                    }

                    //cleanse the name
                    if (uniformName.endsWith("[0]")){
                        uniformName = uniformName.substring(0, uniformName.length() - 3);
                    }

                    var theUniform = new GLShaderUniform(shaderID, uLocation, uniformName, componentType);
                    this.shaderUniforms.put(uniformName, theUniform);

                    System.out.println(" -> uniform " + u + ": " + uniformName + " - type " + enumToType(uType) + ", location " + uLocation);
                } //if it's > -1 then it's in a uniform block
            }

            var numInputAttributes = stack.mallocInt(1);
            values = stack.mallocInt(3);
            properties = stack.ints(GL45.GL_TYPE, GL45.GL_LOCATION, GL45.GL_ARRAY_SIZE);

            GL45.glGetProgramInterfaceiv(shaderID, GL45.GL_PROGRAM_INPUT, GL45.GL_ACTIVE_RESOURCES, numInputAttributes);

            System.out.println(" > num input attributes: " + numInputAttributes.get(0));

            for (int a = 0; a < numInputAttributes.get(0); a++){
                GL45.glGetProgramResourceiv(shaderID, GL45.GL_PROGRAM_INPUT, a, properties, null, values);
                int attrType = values.get(0);
                int location = values.get(1);
                int arraySize = values.get(2);

                var inputName = GL45.glGetProgramResourceName(shaderID, GL45.GL_PROGRAM_INPUT, a);
                System.out.println(" -> attribute / program input " + a + ": " + inputName + " - type " +
                        enumToType(attrType) + " [" + arraySize + "] @ " + location);
            }

            var numOutputAttributes = stack.mallocInt(1);
            GL45.glGetProgramInterfaceiv(shaderID, GL45.GL_PROGRAM_OUTPUT, GL45.GL_ACTIVE_RESOURCES, numOutputAttributes);

            System.out.println(" > num output attributes: " + numOutputAttributes.get(0));
            for (int a = 0; a < numOutputAttributes.get(0); a++){
                GL45.glGetProgramResourceiv(shaderID, GL45.GL_PROGRAM_OUTPUT, a, properties, null, values);
                int attrType = values.get(0);
                int location = values.get(1);
                int arraySize = values.get(2);

                var outputName = GL45.glGetProgramResourceName(shaderID, GL45.GL_PROGRAM_OUTPUT, a);
                System.out.println(" -> program output " + a + ": " + outputName + " - type " +
                        enumToType(attrType) + " [" + arraySize + "] @ " + location);
            }
        }

        return this;
    }
}
