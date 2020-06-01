package com.example.learn_opengl;

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
                    "void main() {" +
                    "  v_color = a_color;" +
                    "  gl_Position = projectMatrix * vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "varying vec4 v_color;\n" +
                    "void main() {" +
                    "  gl_FragColor = v_color;" +
                    "}";


    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {   // in counterclockwise order:
            0.0f, 1.0f, 0.0f, // top
            -1.0f, -1.0f, 0.0f, // bottom left
            1.0f, -1.0f, 0.0f  // bottom right
    };

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {
            1, 0, 0, 1.0f,
            0, 1, 0, 1.0f,
            0, 0, 1, 1.0f,
    };

    public Triangle() {
        vertexBuffer = GLHelper.createFloatBuffer(triangleCoords);

        colorBuffer = GLHelper.createFloatBuffer(color);

        mProgram = GLHelper.makeProgram(vertexShaderCode, fragmentShaderCode);
    }

    int projectMatrixIndex;

    @Override
    public void draw() {

        // get handle to vertex shader's vPosition member
        positionHandle = GLHelper.getAttr(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        int colorHandle = GLHelper.getAttr(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                4 * 4, colorBuffer);

        projectMatrixIndex = GLHelper.getAttr(mProgram, "projectMatrix");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

    @Override
    public void setUpProjectMatrix(float[] projectMatrix) {
        GLES20.glUniformMatrix4fv(projectMatrixIndex, 1, false, projectMatrix, 0);
    }
}
