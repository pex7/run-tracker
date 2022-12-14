package com.example.runtracker

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class LocationService: Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationListeningCallback

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> startService()
            ACTION_STOP -> stopService()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun startService() {
        val notification = NotificationCompat.Builder(this, "locationTracker")
            .setContentTitle("Run Tracker")
            .setContentText("Total distance: ")
            .setSmallIcon(R.drawable.runner)
            .setOngoing(true)


    }

    private fun stopService() {

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