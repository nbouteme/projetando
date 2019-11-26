package com.sleepy2.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.sleepy2.SleepyBird

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.useGL30 = true;
        config.gles30ContextMajorVersion = 3;
        config.gles30ContextMinorVersion = 3;
        config.width = 1280
        config.height = 720
        //config.fullscreen
        LwjglApplication(SleepyBird(), config)
    }
}
