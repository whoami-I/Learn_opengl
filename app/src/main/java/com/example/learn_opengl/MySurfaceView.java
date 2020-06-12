package com.example.learn_opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MySurfaceView extends GLSurfaceView {
    public MySurfaceView(Context context) {
        this(context, null);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
    }

    public interface MoveCallback {
        void move(float x, float y);
    }

    public void setCallback(MoveCallback callback) {
        this.callback = callback;
    }

    public MoveCallback callback;
    private float mLastX = -1;
    private float mLastY = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                mLastX = -1;
                mLastY = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (callback != null) {
                    callback.move(dx, dy);
                }
                mLastX = x;
                mLastY = y;
                requestRender();
                break;
        }
        return true;
    }
}
