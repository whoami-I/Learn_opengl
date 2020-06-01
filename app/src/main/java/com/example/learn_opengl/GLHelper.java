package com.example.learn_opengl;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLHelper {

    public static FloatBuffer createFloatBuffer(float[] fs) {
        ByteBuffer bb = ByteBuffer.allocateDirect(
                fs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(fs);
        buffer.position(0);
        return buffer;
    }

    public static int getAttr(int program, String name) {
        return GLES20.glGetAttribLocation(program, name);
    }

    public static int getUniform(int program, String name) {
        return GLES20.glGetUniformLocation(program, name);
    }

    public static int makeProgram(String vertexShader, String fragmentShader) {
        // 步骤1：编译顶点着色器
        int vertexShaderId = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(vertexShaderId, vertexShader);
        GLES20.glCompileShader(vertexShaderId);


        // 步骤2：编译片段着色器
        int fragmentShaderId = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        // add the source code to the shader and compile it
        GLES20.glShaderSource(fragmentShaderId, fragmentShader);
        GLES20.glCompileShader(fragmentShaderId);


        // 步骤3：将顶点着色器、片段着色器进行链接，组装成一个OpenGL程序
        // create empty OpenGL ES Program
        int program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, vertexShaderId);

        // add the fragment shader to program
        GLES20.glAttachShader(program, fragmentShaderId);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program);

        return program;
    }
}
