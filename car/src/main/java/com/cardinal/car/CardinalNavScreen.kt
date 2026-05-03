package com.cardinal.car

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.Surface
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.SurfaceCallback
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Distance
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.navigation.model.RoutingInfo
import androidx.car.app.navigation.model.Step
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CardinalNavScreen(carContext: CarContext) : Screen(carContext) {

    private var carState = CarNavigationState.state.value
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val surfaceCallback = object : SurfaceCallback {
        override fun onSurfaceAvailable(surfaceContainer: androidx.car.app.SurfaceContainer) {
            renderSurface(surfaceContainer)
        }

        override fun onVisibleAreaChanged(visibleArea: Rect) {
            // Re-render when visible area changes
        }

        override fun onStableAreaChanged(stableArea: Rect) {
            // Re-render when stable area changes
        }

        override fun onSurfaceDestroyed(surfaceContainer: androidx.car.app.SurfaceContainer) {
            // Clean up if needed
        }
    }

    init {
        carContext.getCarService(AppManager::class.java)
            .setSurfaceCallback(surfaceCallback)

        lifecycleScope.launch {
            CarNavigationState.state.collect { state ->
                carState = state
                invalidate()
            }
        }
    }

    private fun renderSurface(surfaceContainer: androidx.car.app.SurfaceContainer) {
        val surface: Surface? = surfaceContainer.surface
        if (surface?.isValid != true) return
        try {
            val canvas: Canvas? = surface.lockCanvas(null)
            if (canvas != null) {
                canvas.drawColor(Color.parseColor("#0A1322"))
                paint.color = Color.WHITE
                paint.textSize = 32f
                paint.textAlign = Paint.Align.CENTER
                val cx = canvas.width / 2f
                val cy = canvas.height / 2f
                canvas.drawText("Cardinal", cx, cy, paint)
                surface.unlockCanvasAndPost(canvas)
            }
        } catch (_: Exception) {
            // Ignore surface errors
        }
    }

    override fun onGetTemplate(): Template {
        val builder = NavigationTemplate.Builder()

        val step = carState.nextStep
        if (carState.isNavigating && step != null) {
            val distance = carState.distanceToNextStepMeters?.let {
                Distance.create(it.toDouble(), Distance.UNIT_METERS)
            } ?: Distance.create(0.0, Distance.UNIT_METERS)

            builder.setNavigationInfo(
                RoutingInfo.Builder()
                    .setCurrentStep(
                        Step.Builder(step.streetName).build(),
                        distance
                    )
                    .build()
            )
        } else {
            builder.setNavigationInfo(
                RoutingInfo.Builder()
                    .setCurrentStep(
                        Step.Builder(carState.currentRoadName ?: "Continue on current road").build(),
                        Distance.create(0.0, Distance.UNIT_METERS)
                    )
                    .build()
            )
        }

        builder.setActionStrip(
            ActionStrip.Builder()
                .addAction(
                    Action.Builder()
                        .setTitle("Search")
                        .setOnClickListener { /* TODO */ }
                        .build()
                )
                .build()
        )

        return builder.build()
    }
}
