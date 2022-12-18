package com.example.runtracker

import android.graphics.Bitmap
import com.mapbox.geojson.Point

enum class Unit {
    MILES,
    KILOMETERS
}

data class MapState(
    val isRunning: Boolean = false,
    val startingPoint: Point? = null,
    val endingPoint: Point? = null,
    val unit: Unit = Unit.MILES,
    val locationPermissionGranted: Boolean = false,
    val isDarkMode: Boolean = false,
    val currentLocation: Pair<Double?, Double?> = Pair(null, null),
    val shouldTakeSnapshot: Boolean = false,
    val snapshot: Bitmap? = null,
    val pathPoints: MutableList<Point> = mutableListOf(),
    val totalDistanceText: String? = null,
)