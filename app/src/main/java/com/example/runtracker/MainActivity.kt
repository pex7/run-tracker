package com.example.runtracker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.runtracker.ui.theme.RunTrackerTheme
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.common.location.compat.permissions.PermissionsListener
import com.mapbox.common.location.compat.permissions.PermissionsManager

class MainActivity : ComponentActivity() {
    lateinit var permissionsManager: PermissionsManager
    lateinit var locationEngine: LocationEngine
    var permissionsListener = getPermissionsListener()
    var latLng: Pair<Double?, Double?> = Pair(null, null)
    private val callback = LocationListeningCallback(this, latLng)

    private var request = LocationEngineRequest.Builder(1000L)
        .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
        .setMaxWaitTime(5000L)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handlePermissions(this, this@MainActivity)

        locationEngine = LocationEngineProvider.getBestLocationEngine(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationEngine.requestLocationUpdates(request, callback, mainLooper)
        locationEngine.getLastLocation(callback)

        setContent {
            RunTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MapScreen(locationCallback = callback)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()

        locationEngine.removeLocationUpdates(callback)
    }

    private fun handlePermissions(context: Context, activity: MainActivity) {
        if (PermissionsManager.areLocationPermissionsGranted(context)) {
            Log.d("$$$$", "Location permissions granted")
        } else {
            permissionsManager = PermissionsManager(permissionsListener)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    private fun getPermissionsListener() = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
            Log.d("$$$$", "permissionsToExplain: $permissionsToExplain")
        }

        override fun onPermissionResult(granted: Boolean) {
            if (granted) {
                Log.d("$$$$", "onPermissionResult: $granted")
            } else {
                Log.d("$$$$", "onPermissionResult: $granted")
            }
        }

    }
}
