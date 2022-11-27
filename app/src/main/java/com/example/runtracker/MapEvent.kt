package com.example.runtracker

import android.graphics.Bitmap

sealed interface MapEvent {
    object OnStartRun : MapEvent
    object OnStopRun : MapEvent
    object OnShareRun : MapEvent
    object OnDarkModeChange : MapEvent
    object OnMetricChange : MapEvent
    object OnSnapshotPress : MapEvent
    object OnRemoveSnapshot : MapEvent
    data class OnLocationChange(val latLng: Pair<Double?, Double?>) : MapEvent
    data class OnSnapshotTaken(val bitmap: Bitmap?) : MapEvent
}