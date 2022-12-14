package com.example.runtracker

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
) {
    val state = viewModel.state
    val metricLabel =
        if (state.metric == Metric.MILES) R.string.miles_label else R.string.kilometers_label
    val runLabel = if (state.isRunning) R.drawable.stop else R.drawable.start
    // no disabled property for FloatingActionButton :(
    val shareColor =
        if (state.endingPoint !== null) MaterialTheme.colors.error else Color.LightGray

    Box(
        modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                ResourceOptionsManager.getDefault(
                    context,
                    context.getString(R.string.mapbox_access_token)
                )
                MapView(context).apply {
                    getMapboxMap().loadStyleUri(
                        Style.MAPBOX_STREETS
                    )
                }
            },
            update = { mapView ->
                mapView.getMapboxMap()
                    .loadStyleUri(if (state.isDarkMode) Style.DARK else Style.MAPBOX_STREETS) {
                        mapView.location.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                        }
                        mapView.viewport.transitionTo(mapView.viewport.makeFollowPuckViewportState())
                        if (state.shouldTakeSnapshot) {
                            Log.d("$$$$", "state.shouldTakeSnapshot: ${state.shouldTakeSnapshot}")
                            mapView.snapshot { bitmap ->
                                viewModel.onEvent(MapEvent.OnSnapshotTaken(bitmap))
                            }
                        }
                    }
                val annotationsApi = mapView.annotations
                val polylineAnnotationManager = annotationsApi.createPolylineAnnotationManager(mapView)
                val points = listOf(
                    fromLngLat(-115.154, 36.1585, ),
                    fromLngLat(-115.153, 36.1584, )
                )
                val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                    .withPoints(points)
                    .withLineColor("#ee4e8b")
                    .withLineWidth(5.0)

                polylineAnnotationManager?.create(polylineAnnotationOptions)
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Switch(
                    checked = state.isDarkMode,
                    onCheckedChange = { viewModel.onEvent(MapEvent.OnDarkModeChange) })
            }
            Text(text = state.currentLocation.toString())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(MapEvent.OnSnapshotPress) },
                    modifier = Modifier.size(44.dp),
                    backgroundColor = shareColor
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = stringResource(id = R.string.take_snapshot_description),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                FloatingActionButton(onClick = {
                    val runEvent = if (state.isRunning) MapEvent.OnStopRun else MapEvent.OnStartRun
                    viewModel.onEvent(runEvent)
                }) {
                    Icon(
                        painter = painterResource(id = runLabel),
                        contentDescription = stringResource(id = R.string.start_run_description)
                    )
                }
                Spacer(modifier = Modifier.padding(horizontal = 12.dp))
                FloatingActionButton(
                    onClick = { viewModel.onEvent(MapEvent.OnMetricChange) },
                    modifier = Modifier.size(44.dp),
                    backgroundColor = MaterialTheme.colors.error
                ) {
                    Text(text = stringResource(id = metricLabel))
                }
            }
        }

        Box() {
            state.snapshot?.asImageBitmap()
                ?.let {
                    Image(
                        bitmap = it,
                        contentDescription = stringResource(id = R.string.snapshot_description)
                    )
                    Button(onClick = { viewModel.onEvent(MapEvent.OnRemoveSnapshot) }) {
                        Text(text = "Close")
                    }
                }
        }
    }
}