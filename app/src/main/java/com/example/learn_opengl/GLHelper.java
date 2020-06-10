package com.example.learn_opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLHelper {

    private static final String TAG = "GLHelper";

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

    public static class TextureBean {
        public int textureId;
        public int width;
        public int height;
    }

    public static TextureBean loadTexture(Context context, int resourceId) {
        TextureBean textureBean = new TextureBean();
        int[] textId = new int[1];
        //1.生成texture对象
        GLES20.glGenTextures(1, textId, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bitmap = BitmapFactory.decodeResource(
                context.getResources(), resourceId, options);

        if (bitmap == null) {
            Log.w(TAG, "Resource ID $resourceId could not be decoded.");
            // 加载Bitmap资源失败，删除纹理Id
            GLES20.glDeleteTextures(1, textId, 0);
            return textureBean;
        }
        // 2. 将纹理绑定到OpenGL对象上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textId[0]);

        // 3. 设置纹理过滤参数:解决纹理缩放过程中的锯齿问题。若不设置，则会导致纹理为黑色
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
        // 4. 通过OpenGL对象读取Bitmap数据，并且绑定到纹理对象上，之后就可以回收Bitmap对象
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        //GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,-1,);
        // 5. 生成Mip位图
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        // 6. 回收Bitmap对象
        textureBean.width = bitmap.getWidth();
        textureBean.height = bitmap.getHeight();
        bitmap.recycle();

        // 7. 将纹理从OpenGL对象上解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        // 所以整个流程中，OpenGL对象类似一个容器或者中间者的方式，将Bitmap数据转移到OpenGL纹理上
        textureBean.textureId = textId[0];
        return textureBean;
    }
}
