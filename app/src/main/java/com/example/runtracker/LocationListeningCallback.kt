package com.example.runtracker

import android.util.Log
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.Exception
import java.lang.ref.WeakReference

class LocationListeningCallback internal constructor(
    activity: MainActivity,
) : LocationEngineCallback<LocationEngineResult> {
    private val activityWeakReference: WeakReference<MainActivity>

    private val _lngLat: MutableStateFlow<Pair<Double?, Double?>> =
        MutableStateFlow(Pair(null, null))
    val latLng: StateFlow<Pair<Double?, Double?>> = _lngLat

    init {
        this.activityWeakReference = WeakReference(activity)
    }

    override fun onSuccess(result: LocationEngineResult?) {
        val lat = result?.lastLocation?.latitude
        val lng = result?.lastLocation?.longitude
        _lngLat.value = Pair(lng, lat)
    }

    override fun onFailure(exception: Exception) {
        Log.d("$$$$", "Location exception: $exception")
    }
}