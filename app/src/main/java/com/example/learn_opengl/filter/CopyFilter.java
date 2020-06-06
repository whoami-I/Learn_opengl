package com.example.learn_opengl.filter;

public class CopyFilter extends BaseFilter{
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "varying vec4 v_color;\n" +
                    "varying vec2 v_TexCoord;\n" +
                    "uniform sampler2D u_TextureUnit;\n" +
                    "void main() {" +
                    "  vec4 color = texture2D(u_TextureUnit, v_TexCoord*5.0);" +
                    "  gl_FragColor = color;" +
                    "}";
    @Override
    public String getFragmentShaderCoder() {
        return fragmentShaderCode;
    }
}
