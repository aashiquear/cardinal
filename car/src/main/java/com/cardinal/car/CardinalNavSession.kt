package com.cardinal.car

import androidx.car.app.Session
import androidx.car.app.Screen

class CardinalNavSession : Session() {
    override fun onCreateScreen(intent: android.content.Intent): Screen {
        return CardinalNavScreen(carContext)
    }
}
