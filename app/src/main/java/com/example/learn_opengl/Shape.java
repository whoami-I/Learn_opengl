package com.example.learn_opengl;

public interface Shape {
    void draw();
    void setUpProjectMatrix(float [] projectMatrix);
    default void onCreate(){}
    default void onSizeChange(int width,int height){}
    default void move(float x,float y){}
}
