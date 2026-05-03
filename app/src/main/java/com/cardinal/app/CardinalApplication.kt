package com.cardinal.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.maplibre.android.MapLibre

@HiltAndroidApp
class CardinalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MapLibre.getInstance(this)
    }
}
