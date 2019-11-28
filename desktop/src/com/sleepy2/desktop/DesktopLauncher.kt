package com.sleepy2.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.sleepy2.SleepyBird

object ScreenConfig : LwjglApplicationConfiguration() {
    init {
        useGL30 = true;
        gles30ContextMajorVersion = 3;
        gles30ContextMinorVersion = 3;
        width = 1600
        height = 900
    }
}

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = ScreenConfig
        //config.fullscreen
        LwjglApplication(SleepyBird(), config)
    }
}
