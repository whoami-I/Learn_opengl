package com.example.learn_opengl;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;


public class FBO implements Shape {

    private int mProgram;
    private int mProgram1;
    private int positionHandle;
    private int positionHandle2;
    private int colorHandle;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "uniform mat4 projectMatrix;\n" +
                    "attribute vec4 a_color;\n" +
                    "varying vec4 v_color;\n" +
                    "attribute vec2 a_TexCoord;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {" +
                    "  v_TexCoord = vec2(a_TexCoord.x, 1. -a_TexCoord.y);" +
                    "  v_color = a_color;" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "uniform sampler2D u_TextureUnit1;\n" +
                    "void main() {" +
                    "vec4 texture = texture2D(u_TextureUnit, v_TexCoord);" +
                    "gl_FragColor = texture;" +
                    "}";

    private final String fragmentShaderCode2 =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "uniform sampler2D u_Texture;\n" +
                    "void main() {" +
                    "gl_FragColor = texture2D(u_Texture, v_TexCoord);" +
                    "}";

    float vertex1[] = {   // in counterclockwise order:
            -1, 1, -1, -1, 1, -1, 1, 1
    };

    float vertex2[] = {   // in counterclockwise order:
            -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f
    };
    private final int vertexCount = vertex1.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private FloatBuffer vertexBuffer;
    private FloatBuffer vertexBuffer2;
    private FloatBuffer colorBuffer;
    int aTexCoordLocation;
    int aTexCoordLocation2;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;
//    static float triangleCoords[] = {   // in counterclockwise order:
//            1.0f, 1.0f, 0.0f, // top
//            -1.0f, 1.0f, 0.0f, // bottom left
//            -1.0f, -1.0f, 0.0f,  // bottom right
//            1,-1,0
//    };


    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {
            1, 0, 0, 1.0f,
            0, 1, 0, 1.0f,
            0, 0, 1, 1.0f,
    };
    FloatBuffer texture_buffer;
    FloatBuffer texture_buffer2;
    float[] texture_pos = {
            0, 1, 0, 0, 1, 0, 1, 1,
    };
    float[] texture_pos2 = {
            0, 1, 0, 0, 1, 0, 1, 1,
    };

    public FBO(Context context) {
        mProgram1 = GLHelper.makeProgram(vertexShaderCode, fragmentShaderCode2);
        mProgram = GLHelper.makeProgram(vertexShaderCode, fragmentShaderCode);


        vertexBuffer = GLHelper.createFloatBuffer(vertex1);
        vertexBuffer2 = GLHelper.createFloatBuffer(vertex2);
        positionHandle = GLHelper.getAttr(mProgram, "vPosition");

        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        colorBuffer = GLHelper.createFloatBuffer(color);
        int colorHandle = GLHelper.getAttr(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                4 * 4, colorBuffer);

        texture_buffer = GLHelper.createFloatBuffer(texture_pos);
        texture_buffer2 = GLHelper.createFloatBuffer(texture_pos2);

        aTexCoordLocation = GLHelper.getAttr(mProgram, "a_TexCoord");
        uTextureUnitLocation = GLHelper.getUniform(mProgram, "u_TextureUnit");
        uTextureUnitLocation1 = GLHelper.getUniform(mProgram, "u_TextureUnit1");
        uTextureUnitLocation2 = GLHelper.getUniform(mProgram1, "u_Texture");
        textureBean = GLHelper.loadTexture(context, R.drawable.pikachu);
        textureBean2 = GLHelper.loadTexture(context, R.drawable.tuzki);
//        // 开启纹理透明混合，这样才能绘制透明图片
    }


    GLHelper.TextureBean textureBean;
    GLHelper.TextureBean textureBean2;
    int uTextureUnitLocation;
    int uTextureUnitLocation1;

    int uTextureUnitLocation2;
    int projectMatrixIndex;

    @Override
    public void draw() {

//        int colorHandle = GLHelper.getAttr(mProgram, "a_color");
//        GLES20.glEnableVertexAttribArray(colorHandle);
//        GLES20.glVertexAttribPointer(colorHandle, COORDS_PER_VERTEX,
//                GLES20.GL_FLOAT, false,
//                4 * 4, colorBuffer);

        //GLES20.glViewport(0, 0, textureBean.width, textureBean.height);
        createEnv();
        bindFrameBufferInfo();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,mFrameBuffer[0]);
        GLES20.glUseProgram(mProgram);
        projectMatrixIndex = GLHelper.getAttr(mProgram, "projectMatrix");
        vertexBuffer.position(0);
        texture_buffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(aTexCoordLocation, 2, GLES20.GL_FLOAT, false, 2 * 4, texture_buffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBean.textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
//
        GLES20.glUseProgram(mProgram1);
        projectMatrixIndex = GLHelper.getUniform(mProgram1, "projectMatrix");
        positionHandle2 = GLHelper.getAttr(mProgram1, "vPosition");
        aTexCoordLocation2 = GLHelper.getAttr(mProgram1, "a_TexCoord");

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(positionHandle2, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glEnableVertexAttribArray(positionHandle2);

        texture_buffer.position(0);
        GLES20.glVertexAttribPointer(aTexCoordLocation2, 2, GLES20.GL_FLOAT, false, 2 * 4, texture_buffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation2);
        uTextureUnitLocation2 = GLHelper.getUniform(mProgram1, "u_Texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
        GLES20.glUniform1i(uTextureUnitLocation2, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
////
        deleteEnv();
    }

    @Override
    public void setUpProjectMatrix(float[] projectMatrix) {
        GLES20.glUniformMatrix4fv(projectMatrixIndex, 1, false, projectMatrix, 0);
    }

    private int[] mFrameBuffer = new int[1];
    private int[] mTexture = new int[1];

    private void createEnv() {
        // 二：FrameBuffer
        // 1. 创建FrameBuffer
        GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
        // 2. 生成纹理对象
        GLES20.glGenTextures(1, mTexture, 0);
        // 3. 绑定纹理对象
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture[0]);
        // 4. 设置纹理对象的相关信息：颜色模式、大小
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                1080, 1930,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        // 纹理过滤参数设置
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

    }

    private void bindFrameBufferInfo() {
        // 绑定FrameBuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        // 将纹理对象挂载到FrameBuffer上，存储颜色信息
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mTexture[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,0);
    }

    private void deleteEnv() {
        GLES20.glDeleteTextures(1, mTexture, 0);
        GLES20.glDeleteFramebuffers(1, mFrameBuffer, 0);
    }
}
