package com.example.runtracker

import android.app.Service
import android.content.Intent
import android.content.Intent.getIntent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class LocationService: Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationListeningCallback
    private var totalDistance: String = ""

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }

        val extras = intent?.extras

        Log.d("$$$$", "extras: $extras")

        val distance = intent?.extras?.getString("totalDistance") ?: ""

        Log.d("$$$$", "totalDistance: $distance")

        totalDistance = distance

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "locationTracker")
            .setContentTitle("Run Tracker")
            .setContentText("Total distance: ")
            .setSmallIcon(R.drawable.runner)
            .setOngoing(true)

        startForeground(1, notification.build())
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}