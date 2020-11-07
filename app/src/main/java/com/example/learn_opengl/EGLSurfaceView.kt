package com.example.learn_opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class EGLSurfaceView : SurfaceView, SurfaceHolder.Callback2 {
    var renderer: GLSurfaceView.Renderer? = null
    var eglThread: EGLThread? = null

    constructor(context: Context) : this(context, null) {

    }

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {

    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

    }

    init {
        holder.addCallback(this)
    }

    fun setRender(render: GLSurfaceView.Renderer) {
        Log.d("TAG", "setRender")
        this.renderer = render
        eglThread = EGLThread(this)
        eglThread?.start()
        eglThread?.sizeChanged(width, height)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        eglThread?.requestExit()
    }

    public fun onPause() {
        eglThread?.onPause()
    }

    public fun onResume(){
        eglThread?.onResume()
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        eglThread?.sizeChanged(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        eglThread?.surfaceCreate()
    }
}