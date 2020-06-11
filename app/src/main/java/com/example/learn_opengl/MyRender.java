package com.example.learn_opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.learn_opengl.filter.Filter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRender implements GLSurfaceView.Renderer {

    private Context mContext;
    private Shape shape;
    private float ratio;
    private final float[] mProjectionMatrix = new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1,
    };

    public MyRender(Context context) {
        mContext = context;
    }

    public MyRender(Shape shape) {
        this.shape = shape;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 1);
        shape = new Coordinate(mContext);
        shape.setUpProjectMatrix(mProjectionMatrix);
        shape.onCreate();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        ratio = ((float) height) / width;
        Matrix.orthoM(mProjectionMatrix, 0, -1, 1,
                -ratio, ratio, 0, 1);
//        Matrix.perspectiveM(mProjectionMatrix,0,45,ratio,0.1f,100.0f);
        shape.setUpProjectMatrix(mProjectionMatrix);
        shape.onSizeChange(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        shape.draw();
    }

    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
