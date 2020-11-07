package com.example.learn_opengl

class EGLThread constructor(surfaceView: EGLSurfaceView) : Thread() {

    private var mOnPause: Boolean = false
    var surfaceView: EGLSurfaceView = surfaceView
    val lock: Any = Any() //可能导致主线程的阻塞，如何解囧？
    var needExit = false
    var hasInitEGL = false
    var hasCreateSurface = false
    var hasSurface = false;
    val eglHelper = EglHelper(surfaceView)
    var width: Int = 0
    var height: Int = 0
    var sizeChanged = true
    override fun run() {
        super.run()
        while (true) {
            synchronized(lock) {
                if (needExit) return
            }
            if (!hasInitEGL) {
                hasInitEGL = true
                eglHelper.initEnv()
            }
            if (hasSurface && hasInitEGL && !mOnPause) {
                if (hasInitEGL && !hasCreateSurface) {
                    eglHelper.createSurface()
                    hasCreateSurface = true
                    surfaceView?.renderer?.onSurfaceCreated(null, null)
                }
                synchronized(lock) {
                    if (sizeChanged) {
                        surfaceView?.renderer?.onSurfaceChanged(null, width, height)
                        sizeChanged = false
                    }
                }
                if (width > 0 && height > 0) {
                    surfaceView?.renderer?.onDrawFrame(null)
                }
                synchronized(lock){
                    if(!mOnPause){
                        eglHelper.swap()
                    }else{
                        hasCreateSurface = false
                    }
                }
                sleep(16)
            }
        }
    }

    fun requestExit() {
        synchronized(lock) {
            needExit = true
        }
    }

    fun sizeChanged(width: Int, height: Int) {
        synchronized(lock) {
            sizeChanged = true
            this.width = width
            this.height = height
        }
    }

    fun surfaceCreate() {
        synchronized(lock) {
            hasSurface = true
        }
    }

    fun onPause() {
        synchronized(lock){
            mOnPause = true
        }
    }

    fun onResume() {
        synchronized(lock){
            mOnPause = false
        }
    }
}