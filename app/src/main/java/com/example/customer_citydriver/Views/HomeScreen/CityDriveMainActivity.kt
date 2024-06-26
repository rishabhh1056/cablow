package com.example.customer_citydriver

import android.app.Application
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.identity.util.Constants
import com.example.customer_citydriver.Views.HomeScreen.LocationViewModel
import com.example.customer_citydriver.Views.HomeScreen.MapViewModel
import com.example.customer_citydriver.databinding.ActivityCityDriveMainBinding
import com.example.customer_citydriver.manager.LocationPermissionHelper
import com.example.customer_citydriver.model.DirectionsResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.ar.core.Point
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.common.LifecycleService
import com.mapbox.geojson.LineString
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder

import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.search.result.ResultAccuracy
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response
import java.lang.ref.WeakReference



class CityDriveMainActivity : AppCompatActivity() {


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




    // inz.. ViewModels
    private val locationViewModel: LocationViewModel by viewModels()
//    private val mapViewModel: MapViewModel by viewModels()
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    //created variables
    private lateinit var dialog: Dialog
    private var backPressedOnce = false
    private lateinit var mapView: MapView

    //create binding obj
    private lateinit var binding: ActivityCityDriveMainBinding

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityDriveMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val point1 = com.mapbox.geojson.Point.fromLngLat(77.209040,28.662726)
        val point2 = com.mapbox.geojson.Point.fromLngLat(77.249940,28.634204)

        // Direction API Call...
          val data =getroute(point1, point2)

          data.observe(this, Observer {
            Log.e("RuteDirection", "onCreate: $it", )
             drawRoute(it)
        })




        //initialize MapView...
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {

            initializeMapView(binding.mapView)

        }

        //viewModels Functions Call..
        locationViewModel.checkGPSStatus()
        initializeMapView(binding.mapView)

        //observe GPS Status
        locationViewModel.isGPSEnabled.observe(this, Observer { isGPSEnabled ->
            if (isGPSEnabled == false) {
                showLocationSettingsDialog()
            } else
                Toast.makeText(this, "Gps is On", Toast.LENGTH_SHORT).show()
        })


        //Bottom Layouts Visibility controller...

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            binding.bottomSheet.height
            peekHeight = 480
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val intent = intent?.extras

        if (intent?.getBoolean("value") == true) {
            binding.cabBookingLayout.visibility = View.VISIBLE
            binding.bookingDetailsLayout.visibility = View.GONE
            binding.driverDerailsLayout.visibility = View.GONE
            val newHight = 370
            val newHightPx = (newHight * resources.displayMetrics.density).toInt()

            val layoutParams = binding.bottomSheet.layoutParams
            layoutParams.height = newHightPx
            binding.bottomSheet.layoutParams = layoutParams
        } else if (intent?.getBoolean("loder") == true) {
            showDialog()

            binding.cabBookingLayout.visibility = View.GONE
            binding.bookingDetailsLayout.visibility = View.GONE
            binding.driverDerailsLayout.visibility = View.VISIBLE

            val newHight = 360
            val newHightPx = (newHight * resources.displayMetrics.density).toInt()

            val layoutParams = binding.bottomSheet.layoutParams
            layoutParams.height = newHightPx
            binding.bottomSheet.layoutParams = layoutParams
        } else {
            binding.cabBookingLayout.visibility = View.GONE
            binding.driverDerailsLayout.visibility = View.GONE
            binding.bookingDetailsLayout.visibility = View.VISIBLE

            val newHight = 400
            val newHightPx = (newHight * resources.displayMetrics.density).toInt()

            val layoutParams = binding.bottomSheet.layoutParams
            layoutParams.height = newHightPx
            binding.bottomSheet.layoutParams = layoutParams
        }


        //floating button click listener...

        binding.menuFloatingButton.setOnClickListener {
            binding.drawerLayout.open()
        }

        binding.locationFloatingButton.setOnClickListener {
            initializeMapView(binding.mapView)
        }


        //set click listeners...

        binding.SedanCabOption.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }

        binding.XuvCabOption.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }

        binding.miniCabOption.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }

        binding.bickCabOption.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }

        binding.AutoCabOption.setOnClickListener {
            startActivity(Intent(this, PaymentMethodActivity::class.java))
        }


        binding.menuFloatingButton.setOnClickListener {
            binding.drawerLayout.open()
        }


        binding.EtSearchLocation.setOnClickListener {
            startActivity(Intent(this, LocationSearchActivity::class.java))
            binding.EtSearchLocation.isEnabled = false
        }

        binding.carBook.setOnClickListener {
            startActivity(Intent(this, LocationSearchActivity::class.java))
            binding.carBook.isEnabled = false
        }

        binding.autoBook.setOnClickListener {
            startActivity(Intent(this, LocationSearchActivity::class.java))
            binding.autoBook.isEnabled = false
        }

        binding.bikeBook.setOnClickListener {
            startActivity(Intent(this, LocationSearchActivity::class.java))
            binding.bikeBook.isEnabled = false
        }

        binding.callDriver.setOnClickListener {
            openDialer()
        }


    }





    fun getroute (origin:com.mapbox.geojson.Point, destination: com.mapbox.geojson.Point) : MutableLiveData<DirectionsRoute> {

       val _mutableData = MutableLiveData<DirectionsRoute>()
        val client = MapboxDirections.builder()
            .origin(origin)
            .destination(destination)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(getString(R.string.mapbox_access_token))
            .build()





        client?.enqueueCall(object :
            retrofit2.Callback<com.mapbox.api.directions.v5.models.DirectionsResponse?> {
            override fun onResponse(
                call: Call<com.mapbox.api.directions.v5.models.DirectionsResponse?>,
                response: Response<com.mapbox.api.directions.v5.models.DirectionsResponse?>
            ) {


                if (response.body() == null) {
                    Log.e(
                        "Direction",
                        "No routes found, make sure you set the right user and access token."
                    )
                    return
                } else if (response.body()!!.routes().size < 1) {
                    Log.e("Direction", "No routes found")

                    return
                }

                // Get the directions route


                val currentRoute = response.body()!!.routes()[0]

                _mutableData.value = currentRoute
                Log.e("LiveData", "getroute: ${_mutableData.value}", )

            }

            override fun onFailure(
                call: Call<com.mapbox.api.directions.v5.models.DirectionsResponse?>,
                t: Throwable
            ) {
                Log.e("TAG", "onFailure: ${t.message}",)
            }
        })


        return _mutableData
    }



    private fun showDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialogbox)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 3000)
    }


    override fun onResume() {
        super.onResume()
        binding.EtSearchLocation.isEnabled = true
        binding.carBook.isEnabled = true
        binding.autoBook.isEnabled = true
        binding.bikeBook.isEnabled = true
    }


    /* private fun addPointToMap(latitude: Double, longitude: Double) {
        // Create a PointAnnotationManager
        val annotationPlugin = AnnotationPlugin.getAnnotationPlugin(mapView)
        val pointAnnotationManager = annotationPlugin.createPointAnnotationManager()

        // Create a Point
        val point = Point.fromLngLat(longitude, latitude)

        // Create PointAnnotationOptions
        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)

        // Add the point to the map
        pointAnnotationManager.create(pointAnnotationOptions)
    }*/


    /* override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }*/


    private fun showLocationSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Services")
        builder.setMessage("Location services are disabled. Do you want to enable them?")
        builder.setPositiveButton("Yes") { _, _ ->
            // Open settings to enable location services
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun openDialer() {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:8437837835")
        startActivity(intent)
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


        val data = getroute(point1, point2)

        data.observe(this, Observer {
            Log.e("RuteDirection", "onCreate: $it", )
        })



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







    fun initializeMapView(mapView: MapView) {
        this.mapView = mapView
        onMapReady()
    }





    private fun drawRoute(route: DirectionsRoute) {
        val precision = 6
        val routeLineString = LineString.fromPolyline(route.geometry() ?: "", precision)
        val routeSource = geoJsonSource("route-source") {
            geometry(routeLineString)
        }

        mapView.getMapboxMap().getStyle { style ->
            style.addSource(routeSource)

            val routeLayer = lineLayer("route-layer", "route-source") {
                lineColor("blue")
                lineWidth(5.0)
            }

            style.addLayer(routeLayer)
        }
    }







}






