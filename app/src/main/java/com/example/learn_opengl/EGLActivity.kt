package com.example.learn_opengl

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_egl.*

/**
 * egl配置例子
 * 创建自己的EGL配置，实现在surfaceview上面展现一张图片
 * egl相对于GLSurfaceview有更大的灵活性，能够在多个surface之间
 * 共享资源
 */
class EGLActivity :AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_egl)
//        surface_view.postDelayed({
            surface_view.setRender(MyRender(this.applicationContext))
//        },5000)
    }
}