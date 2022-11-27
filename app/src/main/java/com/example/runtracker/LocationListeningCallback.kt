package com.example.runtracker

import android.util.Log
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import java.lang.Exception
import java.lang.ref.WeakReference

class LocationListeningCallback internal constructor(activity: MainActivity, var latLng: Pair<Double?, Double?>) :
    LocationEngineCallback<LocationEngineResult> {

    private val activityWeakReference: WeakReference<MainActivity>

    init {
        this.activityWeakReference = WeakReference(activity)
    }

    override fun onSuccess(result: LocationEngineResult?) {
        val lat = result?.lastLocation?.latitude
        val lng = result?.lastLocation?.longitude
        this.latLng = Pair(lat, lng)
    }

    override fun onFailure(exception: Exception) {
        Log.d("$$$$", "Location exception: $exception")
    }
}