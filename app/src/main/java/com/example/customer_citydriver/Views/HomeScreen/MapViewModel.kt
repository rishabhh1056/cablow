package com.example.customer_citydriver.Views.HomeScreen

import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.customer_citydriver.R
import com.example.customer_citydriver.databinding.ActivityMainBinding
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.core.constants.Constants.PRECISION_6
import com.mapbox.geojson.LineString
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import retrofit2.Call
import retrofit2.Response

class MapViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var mapView: MapView

    val directionApi = GetRoute()



    private val onMoveListener = object : OnMoveListener {
        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(it)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView.mapboxMap.setCamera(CameraOptions.Builder().bearing(it).build())
    }

    fun initializeMapView(mapView: MapView) {
        this.mapView = mapView
        onMapReady()
    }

    private fun onMapReady() {
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .zoom(12.0)
                .build()
        )
        initLocationComponent()
        setupGesturesListener()
        loadMapStyle(Style.MAPBOX_STREETS)

        //start code for direction api
        val point1 = com.mapbox.geojson.Point.fromLngLat(77.209040,28.662726)
        val point2 = com.mapbox.geojson.Point.fromLngLat(77.249940,28.634204)

        val rute =directionApi.FoundRoute(point1, point2, getString(getApplication<Application>().applicationContext,R.string.mapbox_access_token))



        //end code for direction api

    }





    private fun loadMapStyle(styleUrl: String) {
        mapView.mapboxMap.loadStyle(styleUrl) {}
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            puckBearing = PuckBearing.COURSE
            puckBearingEnabled = true
            enabled = true
            locationPuck = LocationPuck2D(
                bearingImage = ImageHolder.from(R.drawable.my_location),
                scaleExpression = Expression.interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed() {
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}
