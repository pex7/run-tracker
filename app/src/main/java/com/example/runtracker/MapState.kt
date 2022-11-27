package com.example.runtracker

import android.graphics.Bitmap

enum class Metric {
    MILES,
    KILOMETERS
}

data class MapState(
    val isRunning: Boolean = false,
    val startingPoint: Long? = null,
    val endingPoint: Long? = null,
    val metric: Metric = Metric.MILES,
    val locationPermissionGranted: Boolean = false,
    val isDarkMode: Boolean = false,
    val currentLocation: Pair<Double?, Double?> = Pair(null, null),
    val shouldTakeSnapshot: Boolean = false,
    val snapshot: Bitmap? = null
)