package com.example.runtracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runtracker.MapEvent.OnDarkModeChange
import com.example.runtracker.MapEvent.OnStartRun
import com.example.runtracker.MapEvent.OnStopRun
import com.example.runtracker.MapEvent.OnShareRun
import com.example.runtracker.MapEvent.OnMetricChange
import com.example.runtracker.MapEvent.OnLocationChange
import com.example.runtracker.MapEvent.OnSnapshotPress
import com.example.runtracker.MapEvent.OnSnapshotTaken
import com.example.runtracker.MapEvent.OnRemoveSnapshot
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(
    private val latLng: StateFlow<Pair<Double?, Double?>>
) : ViewModel() {
    var state by mutableStateOf(MapState())
        private set

    init {
        viewModelScope.launch {
            latLng.collect {
                state = state.copy(currentLocation = it)

                if (it.first !== null && it.second !== null) {
                    state = state.copy(
                        pathPoints = state.pathPoints.plus(
                            Point.fromLngLat(
                                it.first!!,
                                it.second!!
                            )
                        ).toMutableList()
                    )
                }
            }
        }
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            is OnDarkModeChange -> {
                state = state.copy(isDarkMode = !state.isDarkMode)
            }
            is OnStartRun -> {
                state = state.copy(isRunning = true)
            }
            is OnStopRun -> {
                state = state.copy(
                    isRunning = false,
                    endingPoint = state.pathPoints.last()
                )
            }
            is OnShareRun -> {

            }
            is OnMetricChange -> {
                state = state.copy(metric = changeMetric())
            }
            is OnLocationChange -> {
                state = state.copy(currentLocation = event.latLng)
            }
            is OnSnapshotPress -> {
                state = state.copy(shouldTakeSnapshot = true)
            }
            is OnSnapshotTaken -> {
                state = state.copy(snapshot = event.bitmap, shouldTakeSnapshot = false)
            }
            is OnRemoveSnapshot -> {
                state = state.copy(snapshot = null)
            }
        }
    }

    private fun changeMetric(): Metric =
        if (state.metric == Metric.MILES) Metric.KILOMETERS else Metric.MILES
}