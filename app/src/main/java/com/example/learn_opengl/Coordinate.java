package com.example.learn_opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;


public class Coordinate implements Shape {

    private int mProgram;
    private int positionHandle;
    private int colorHandle;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "uniform mat4 projectMatrix;\n" +
                    "uniform mat4 modelMatrix;\n" +
                    "uniform mat4 viewMatrix;\n" +
                    "attribute vec4 a_color;\n" +
                    "varying vec4 v_color;\n" +
                    "attribute vec2 a_TexCoord;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {" +
                    "  v_TexCoord = vec2(a_TexCoord.x, 1. -a_TexCoord.y);" +
                    "  v_color = a_color;" +
                    "  gl_Position = projectMatrix*viewMatrix*modelMatrix * vPosition;" +
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
                    "vec4 texture1 = texture2D(u_TextureUnit1, v_TexCoord);" +
                    "gl_FragColor = texture1*0.5f+texture*0.5f;" +
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

    float[] modelMatrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };


    public Coordinate(Context context) {
        mProgram = GLHelper.makeProgram(vertexShaderCode, fragmentShaderCode);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearDepthf(1.0f);
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
        textureBean = GLHelper.loadTexture(context, R.drawable.pikachu);
        textureBean2 = GLHelper.loadTexture(context, R.drawable.tuzki);
//        // 开启纹理透明混合，这样才能绘制透明图片

        projectMatrixIndex = GLHelper.getUniform(mProgram, "projectMatrix");
        modelMatrixIndex = GLHelper.getUniform(mProgram, "modelMatrix");
        viewMatrixIndex = GLHelper.getUniform(mProgram, "viewMatrix");

//        Matrix.rotateM(modelMatrix, 0, 180.0f, 1.0f, 0.0f, 0.0f);
//        Matrix.translateM(modelMatrix,0,0.2f,0.2f,0);
//        Matrix.scaleM(modelMatrix,0,2,2,2);
        GLES20.glUniformMatrix4fv(modelMatrixIndex, 1, false, modelMatrix, 0);

        float[] viewMatrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        GLES20.glUniformMatrix4fv(viewMatrixIndex, 1, false, viewMatrix, 0);
    }

    @Override
    public void onSizeChange(int width, int height) {
        float[] projectMatrix = new float[]{
                0, 0, 4, 0,
                0, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 0, 1,
        };
        float ratio = ((float) height) / width;
        Matrix.orthoM(projectMatrix, 0, -1, 1,
                -ratio, ratio, -10.0f, 100.0f);
//        Matrix.perspectiveM(projectMatrix,0,45.0f,ratio,0.1f,100.0f);
        GLES20.glUniformMatrix4fv(projectMatrixIndex, 1, false, projectMatrix, 0);
    }

    GLHelper.TextureBean textureBean;
    GLHelper.TextureBean textureBean2;
    int uTextureUnitLocation;
    int uTextureUnitLocation1;
    int projectMatrixIndex;
    int modelMatrixIndex;
    int viewMatrixIndex;

    @Override
    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(aTexCoordLocation, 2, GLES20.GL_FLOAT, false, 2 * 4, texture_buffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBean.textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBean2.textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 1);

        GLES20.glUniformMatrix4fv(modelMatrixIndex, 1, false, modelMatrix, 0);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

    }

    @Override
    public void setUpProjectMatrix(float[] projectMatrix) {
        //GLES20.glUniformMatrix4fv(projectMatrixIndex, 1, false, projectMatrix, 0);
    }

    @Override
    public void move(float x, float y) {
        Matrix.rotateM(modelMatrix, 0, 180.0f*x/320, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, y*180/320.f, 1.0f, 0.0f, 0.0f);
        GLES20.glUniformMatrix4fv(modelMatrixIndex, 1, false, modelMatrix, 0);
    }
}
