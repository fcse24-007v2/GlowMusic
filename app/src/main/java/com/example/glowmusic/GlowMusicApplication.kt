package com.example.glowmusic

import android.app.Application
import com.example.glowmusic.di.AppContainer
import com.example.glowmusic.di.DefaultAppContainer

class GlowMusicApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
