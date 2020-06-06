package com.example.learn_opengl.filter;

import android.opengl.GLES20;

import com.example.learn_opengl.GLHelper;

public class CrossFilter extends BaseFilter {
    private int transYPos;
    private int transXPos;

    @Override
    public String getFragmentShaderCoder() {
        String fragmentShaderCode = "precision mediump float;" +
                "uniform vec4 vColor;" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_TexCoord;\n" +
                "uniform sampler2D u_TextureUnit;\n" +
                "uniform float transX;\n" +
                "uniform float transY;\n" +
                "vec2 traslate(vec2 srcCoord,float dx,float dy){" +
                "       if(mod(srcCoord.y,0.25) > 0.125){" +
                "           return vec2(srcCoord.x+dx,srcCoord.y+dy);" +
                "       }else{" +
                "           return vec2(srcCoord.x-dx,srcCoord.y-dy);" +
                "       }" +
                "}\n" +
                "void main() {" +
                "   vec2 finalCoord = traslate(v_TexCoord,transX,transY);" +
                "   vec4 color = texture2D(u_TextureUnit, finalCoord);" +
                "   if (finalCoord.x >= 0.0 && finalCoord.x <= 1.0 &&" +
                "       finalCoord.y >= 0.0 && finalCoord.y <= 1.0) {" +
                "       gl_FragColor = color;" +
                "   }"+
                "}";
        return fragmentShaderCode;
    }

    long startTime;

    @Override
    public void onCreate(int program) {
        super.onCreate(program);
        startTime = System.currentTimeMillis();
        transXPos = GLHelper.getUniform(program, "transX");
        transYPos = GLHelper.getUniform(program, "transY");
    }

    @Override
    public void draw() {
        super.draw();
        long millis = System.currentTimeMillis();
        float dx = (float) (Math.sin((millis - startTime) / 1000.0) * 0.5);
        GLES20.glUniform1f(transXPos, dx);
        GLES20.glUniform1f(transYPos, 0.0f);
    }
}
