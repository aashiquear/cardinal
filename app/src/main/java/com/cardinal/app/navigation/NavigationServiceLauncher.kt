package com.cardinal.app.navigation

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationServiceLauncher @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun start() {
        val intent = Intent(context, NavigationForegroundService::class.java)
        context.startForegroundService(intent)
    }

    fun stop() {
        val intent = Intent(context, NavigationForegroundService::class.java)
        context.stopService(intent)
    }
}
