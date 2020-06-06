package com.example.learn_opengl.filter;

public abstract class BaseFilter {
    abstract public String getFragmentShaderCoder();
    public void draw(){}
    public void onCreate(int program){}
}
