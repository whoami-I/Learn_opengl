package com.example.learn_opengl;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;



public class Triangle implements Shape {

    private int mProgram;
    private int positionHandle;
    private int colorHandle;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "uniform mat4 projectMatrix;\n" +
                    "attribute vec4 a_color;\n" +
                    "varying vec4 v_color;\n" +
                    "attribute vec2 a_TexCoord;\n"+
                    "varying vec2 v_TexCoord;\n"+
                    "void main() {" +
                    "  v_TexCoord = vec2(a_TexCoord.x, 1. -a_TexCoord.y);" +
                    "  v_color = a_color;" +
                    "  gl_Position = projectMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_TexCoord;\n"+
                    "uniform sampler2D u_TextureUnit;\n"+
                    "void main() {" +
                    "  gl_FragColor = texture2D(u_TextureUnit, v_TexCoord);" +
                    //"  gl_FragColor = v_color;" +
                    "}";


    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;
//    static float triangleCoords[] = {   // in counterclockwise order:
//            1.0f, 1.0f, 0.0f, // top
//            -1.0f, 1.0f, 0.0f, // bottom left
//            -1.0f, -1.0f, 0.0f,  // bottom right
//            1,-1,0
//    };

    static float triangleCoords[] = {   // in counterclockwise order:
            //-1,-1,1,-1,-1,1,1,1
            -1,1,-1,-1,1,-1,1,1
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {
            1, 0, 0, 1.0f,
            0, 1, 0, 1.0f,
            0, 0, 1, 1.0f,
    };

    float [] texture_pos={
            0f, 1f, 0f, 0f, 1f, 0f, 1f, 1f
            //0,0,1,0,0,1,1,1
    };
    public Triangle(Context context) {
        mProgram = GLHelper.makeProgram(vertexShaderCode, fragmentShaderCode);


        vertexBuffer = GLHelper.createFloatBuffer(triangleCoords);
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

        FloatBuffer texture_buffer = GLHelper.createFloatBuffer(texture_pos);
        int aTexCoordLocation = GLHelper.getAttr(mProgram,"a_TexCoord");
        uTextureUnitLocation = GLHelper.getUniform(mProgram,"u_TextureUnit");
        GLES20.glVertexAttribPointer(aTexCoordLocation, 2, GLES20.GL_FLOAT, false, 2*4, texture_buffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);
        textureBean = GLHelper.loadTexture(context, R.drawable.pikachu);
//        // 开启纹理透明混合，这样才能绘制透明图片
    }


    GLHelper.TextureBean textureBean;
    int uTextureUnitLocation;
    int projectMatrixIndex;

    @Override
    public void draw() {

//        int colorHandle = GLHelper.getAttr(mProgram, "a_color");
//        GLES20.glEnableVertexAttribArray(colorHandle);
//        GLES20.glVertexAttribPointer(colorHandle, COORDS_PER_VERTEX,
//                GLES20.GL_FLOAT, false,
//                4 * 4, colorBuffer);

        projectMatrixIndex = GLHelper.getAttr(mProgram, "projectMatrix");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureBean.textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
    }

    @Override
    public void setUpProjectMatrix(float[] projectMatrix) {
        GLES20.glUniformMatrix4fv(projectMatrixIndex, 1, false, projectMatrix, 0);
    }
}
