package com.example.learn_opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import androidx.core.math.MathUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class RotateCubic implements Shape {

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
                    "gl_FragColor = texture;" +
                    "}";

    float vertex1[] = {   // in counterclockwise order:
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,

            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,

            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
    };
    short indice[] = {
            6, 7, 4, 6, 4, 5,    //后面
            6, 3, 7, 6, 2, 3,    //右面
            6, 5, 1, 6, 1, 2,    //下面
            0, 3, 2, 0, 2, 1,    //正面
            0, 1, 5, 0, 5, 4,    //左面
            0, 7, 3, 0, 4, 7,    //上面
    };
    float[] texture_pos = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,

            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,

            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,

            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,

            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    float vertex2[] = {   // in counterclockwise order:
            -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f
    };
    private final int vertexCount = vertex1.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer colorBuffer;
    int aTexCoordLocation;
    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
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


    float[] modelMatrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };


    public RotateCubic(Context context) {
        mProgram = GLHelper.makeProgram(vertexShaderCode, fragmentShaderCode);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClearDepthf(1.0f);
        vertexBuffer = GLHelper.createFloatBuffer(vertex1);
        indexBuffer = GLHelper.createShortBuffer(indice);
        positionHandle = GLHelper.getAttr(mProgram, "vPosition");
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        colorBuffer = GLHelper.createFloatBuffer(color);
        int colorHandle = GLHelper.getAttr(mProgram, "a_color");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, colorBuffer);

        texture_buffer = GLHelper.createFloatBuffer(texture_pos);

        aTexCoordLocation = GLHelper.getAttr(mProgram, "a_TexCoord");
        uTextureUnitLocation = GLHelper.getUniform(mProgram, "u_TextureUnit");
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
        GLES20.glVertexAttribPointer(aTexCoordLocation, 2, GLES20.GL_FLOAT, false, 0, texture_buffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBean.textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);

        if (x > 0 && y > 0) {
            y = -y;
        } else if (x <= 0 && y > 0) {
            x = -x;
            distance = -distance;
        } else if (x > 0 && y < 0) {
            y = -y;
        } else if (x <= 0 && y < 0) {
            y = -y;
        }
//        y=-y;
        if (distance != 0) {
            Matrix.rotateM(modelMatrix, 0, distance, -y, x, 0.0f);
        }
        GLES20.glUniformMatrix4fv(modelMatrixIndex, 1, false, modelMatrix, 0);
        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indice.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    }

    @Override
    public void setUpProjectMatrix(float[] projectMatrix) {
        //GLES20.glUniformMatrix4fv(projectMatrixIndex, 1, false, projectMatrix, 0);
    }

    float x;
    float y;
    float distance;

    @Override
    public void move(float x, float y) {
        this.x = x;
        this.y = y;
        this.distance = (float) Math.hypot(x, y);

    }
}
