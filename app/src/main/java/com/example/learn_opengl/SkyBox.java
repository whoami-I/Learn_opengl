package com.example.learn_opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;


public class SkyBox implements Shape {

    private int m_LightColorLoc;
    private int m_LightPosLoc;
    private int mProgram;
    private int positionHandle;
    private int colorHandle;
    private int m_veritcalNormalLoc;
    private int m_viewPosLoc;

    public void test() {

    }

    private final String vertexShaderCode =
            "attribute vec4 vPosition;\n" +
                    "uniform mat4 projectMatrix;\n" +
                    "uniform mat4 modelMatrix;\n" +
                    "uniform mat4 viewMatrix;\n" +
                    "attribute vec3 a_normal;\n" +
                    "uniform vec3 lightPos;\n" +
                    "uniform vec3 lightColor;\n" +
                    "uniform vec3 viewPos;\n" +
                    "varying vec3 ambient;\n" +
                    "varying vec3 diffuse;\n" +
                    "varying vec3 specular;\n" +
                    "attribute vec4 a_color;\n" +
                    "varying vec4 v_color;\n" +
                    "attribute vec2 a_TexCoord;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "void main() {" +
                    "  v_TexCoord = vec2(a_TexCoord.x, 1. -a_TexCoord.y);" +
                    "  v_color = a_color;" +
                    "  gl_Position = projectMatrix*viewMatrix*modelMatrix * vPosition;" +
                    "    vec3 fragPos = vec3(modelMatrix * vPosition);\n" +
                    "    // Ambient\n" +
                    "    float ambientStrength = 0.3;\n" +
                    "    ambient = ambientStrength * lightColor;\n" +

                    "    // Diffuse\n" +
                    "    float diffuseStrength = 0.8;\n" +
                    "    vec3 unitNormal = normalize(vec3(modelMatrix * vec4(a_normal, 1.0)));\n" +
                    "    vec3 lightDir = normalize(lightPos - fragPos);\n" +
                    "    float diff = max(dot(unitNormal, lightDir), 0.0);\n" +
                    "    diffuse = diffuseStrength * diff * lightColor;\n" +

                    "    // Specular\n" +
                    "    float specularStrength = 0.5;\n" +
                    "    vec3 viewDir = normalize(viewPos - fragPos);\n" +
                    "    vec3 reflectDir = reflect(-lightDir, unitNormal);\n" +
                    "    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 16.0);\n" +
                    "    specular = specularStrength * spec * lightColor;\n" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "uniform sampler2D u_TextureUnit1;\n" +
                    "varying vec3 ambient;\n" +
                    "varying vec3 diffuse;\n" +
                    "varying vec3 specular;\n" +
                    "void main() {" +
                    "vec4 texture = texture2D(u_TextureUnit, v_TexCoord);" +
                    " vec3 finalColor = vec3(texture);" +
                    "gl_FragColor = vec4(finalColor,1.0);" +
                    "}";

    float vertex1[] = {   // in counterclockwise order:
//            -0.5f, -0.5f, -0.5f,  //背面
//            0.5f, -0.5f, -0.5f,
//            0.5f, 0.5f, -0.5f,
//            0.5f, 0.5f, -0.5f,
//            -0.5f, 0.5f, -0.5f,
//            -0.5f, -0.5f, -0.5f,

            0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,
            0.5f,0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,

            -0.5f, -0.5f, 0.5f, //正面
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,

            -0.5f, -0.5f, 0.5f,
            -0.5f,-0.5f, -0.5f,
            -0.5f,0.5f, -0.5f, // 左侧
            -0.5f,0.5f, -0.5f,
            -0.5f,0.5f, 0.5f,
            -0.5f,-0.5f, 0.5f,

            0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,0.5f,0.5f,//右侧
            0.5f,0.5f,0.5f,
            0.5f,0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,

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
    private FloatBuffer verticalNormalBuffer;
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


    public SkyBox(Context context) {
        mProgram = GLHelper.makeProgram(vertexShaderCode, fragmentShaderCode);

        //init location
        m_LightColorLoc = GLHelper.getUniform(mProgram, "lightColor");
        m_LightPosLoc = GLHelper.getUniform(mProgram, "lightPos");
        m_veritcalNormalLoc = GLHelper.getAttr(mProgram, "a_normal");
        m_viewPosLoc = GLHelper.getUniform(mProgram, "viewPos");


        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//        GLES20.glClearDepthf(1.0f);
//        for (int i = 0; i < vertex1.length; i++) {
//            vertex1[i] = vertex1[i] *;
//        }
        vertexBuffer = GLHelper.createFloatBuffer(vertex1);
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
        textureBean = GLHelper.loadTexture(context, R.drawable.sky);
//        // 开启纹理透明混合，这样才能绘制透明图片

        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(aTexCoordLocation, 2, GLES20.GL_FLOAT, false, 0, texture_buffer);
        GLES20.glEnableVertexAttribArray(aTexCoordLocation);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBean.textureId);
        GLES20.glUniform1i(uTextureUnitLocation, 0);


        verticalNormalBuffer = GLHelper.createFloatBuffer(verticalNormal);
        GLES20.glEnableVertexAttribArray(m_veritcalNormalLoc);
        GLES20.glVertexAttribPointer(m_veritcalNormalLoc, 3, GLES20.GL_FLOAT, false, 0, verticalNormalBuffer);

        projectMatrixIndex = GLHelper.getUniform(mProgram, "projectMatrix");
        modelMatrixIndex = GLHelper.getUniform(mProgram, "modelMatrix");
        viewMatrixIndex = GLHelper.getUniform(mProgram, "viewMatrix");

        GLES20.glUniformMatrix4fv(modelMatrixIndex, 1, false, modelMatrix, 0);

        float[] viewMatrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
        GLES20.glUniformMatrix4fv(viewMatrixIndex, 1, false, viewMatrix, 0);

        //光照相关参数
        GLES20.glUniform3fv(m_LightColorLoc, 1, lightColor, 0);
        GLES20.glUniform3fv(m_LightPosLoc, 1, lightDir, 0);
        GLES20.glUniform3fv(m_viewPosLoc, 1, viewDir, 0);
    }

    @Override
    public void onSizeChange(int width, int height) {
        float[] projectMatrix = new float[16];
        //float[]  = new float[16];
        float ratio = ((float) height) / width;
        Matrix.perspectiveM(projectMatrix, 0, 90, 1.0f * width / height, 0.1f, 100.0f);
//        Matrix.perspectiveM(projectMatrix,0,45.0f,ratio,0.1f,100.0f);
        float[] viewM = new float[16];
        Matrix.setIdentityM(viewM, 0);
        Matrix.setLookAtM(viewM, 0, 0, 0, 0.5f, 0, 0, 0, 0, 1, 0);
        float[] resultM = new float[16];
        Matrix.multiplyMM(resultM, 0, projectMatrix, 0, viewM, 0);
        GLES20.glUniformMatrix4fv(projectMatrixIndex, 1, false, resultM, 0);
    }

    GLHelper.TextureBean textureBean;
    GLHelper.TextureBean textureBean2;
    int uTextureUnitLocation;
    int uTextureUnitLocation1;
    int projectMatrixIndex;
    int modelMatrixIndex;
    int viewMatrixIndex;
    float[] tmp = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };

    @Override
    public void draw() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        y = -y;
        Matrix.setIdentityM(tmp, 0);
        if (distance != 0) {
            //这里要旋转-distance，因为我们的眼睛是在正方体的内部
            Matrix.rotateM(tmp, 0, -Math.abs(x) / 3.0f, 0, x > 0 ? 1.0f : -1.0f, 0);
            Matrix.multiplyMM(tmp, 0, tmp, 0, modelMatrix, 0);
            float[] ttmp = modelMatrix;
            modelMatrix = tmp;
            tmp = ttmp;
        }
        GLES20.glUniformMatrix4fv(modelMatrixIndex, 1, false, modelMatrix, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
    }

    float[] lightColor = new float[]{1.0f, 1.0f, 1.0f};
    float[] lightDir = new float[]{-2.0f, 0.0f, 2.0f};
    float[] viewDir = new float[]{-2.0f, 0.0f, 2.0f};

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

    float verticalNormal[] = {
            //position            //texture coord  //normal
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,

            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,

            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,

            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,

            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,

            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
    };
}
