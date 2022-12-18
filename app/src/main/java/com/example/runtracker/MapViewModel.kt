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
import com.example.runtracker.MapEvent.OnUnitChange
import com.example.runtracker.MapEvent.OnLocationChange
import com.example.runtracker.MapEvent.OnSnapshotPress
import com.example.runtracker.MapEvent.OnSnapshotTaken
import com.example.runtracker.MapEvent.OnRemoveSnapshot
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

private const val KILOMETERS_IN_ONE_MILE = 1.60934

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
                    endingPoint = state.pathPoints.last(),
                    totalDistanceText = getTotalDistance()
                )
            }
            is OnShareRun -> {

            }
            is OnUnitChange -> {
                state = state.copy(
                    unit = changeUnit(),
                    totalDistanceText = changeDistanceUnit()
                )
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

    private fun changeUnit(): Unit =
        if (state.unit == Unit.MILES) Unit.KILOMETERS else Unit.MILES

    private fun Double.formatDistance() = "%.2f".format(this)

    private fun getTotalDistance() = TurfMeasurement.distance(
        state.pathPoints.first(),
        state.pathPoints.last(),
        state.unit.name.lowercase(Locale.ROOT)
    ).formatDistance()

    private fun changeDistanceUnit() =
        if (state.unit == Unit.MILES) {
            state.totalDistanceText?.milesToKilometers()
        } else {
            state.totalDistanceText?.kilometersToMiles()
        }

    private fun String.milesToKilometers() =
        (this.toDouble() * KILOMETERS_IN_ONE_MILE).formatDistance()

    private fun String.kilometersToMiles() =
        (this.toDouble() / KILOMETERS_IN_ONE_MILE).formatDistance()
}