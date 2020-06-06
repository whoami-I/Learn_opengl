package com.example.learn_opengl.filter;

import android.opengl.GLES20;

import com.example.learn_opengl.GLHelper;

public class ScaleFilter extends BaseFilter {
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "uniform float u_scale;\n" +
                    "void main() {" +
                    "  vec4 color = texture2D(u_TextureUnit, (v_TexCoord-0.5)/u_scale+0.5);" +
                    "  gl_FragColor = color;" +
                    "}";
    private int scalePos;

    @Override
    public String getFragmentShaderCoder() {
        return fragmentShaderCode;
    }

    long startTime;

    @Override
    public void onCreate(int program) {
        super.onCreate(program);
        startTime = System.currentTimeMillis();
        scalePos = GLHelper.getUniform(program, "u_scale");
    }

    @Override
    public void draw() {
        super.draw();
        long millis = System.currentTimeMillis();
        float dx = (float) Math.sin((millis - startTime) / 1000.0) * 0.3f + 0.7f;
        GLES20.glUniform1f(scalePos, 1 / dx);
    }
}
