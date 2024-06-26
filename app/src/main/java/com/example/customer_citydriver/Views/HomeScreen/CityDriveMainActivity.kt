package com.example.customer_citydriver.Views.HomeScreen

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import com.example.customer_citydriver.LocationSearchActivity
import com.example.customer_citydriver.PaymentMethodActivity
import com.example.customer_citydriver.R
import com.example.customer_citydriver.databinding.ActivityCityDriveMainBinding
import com.example.customer_citydriver.manager.LocationPermissionHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import java.lang.ref.WeakReference

class CityDriveMainActivity : AppCompatActivity() {


    // inz.. ViewModels
    private val locationViewModel : LocationViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private lateinit var locationPermissionHelper: LocationPermissionHelper

    //created variables
    private lateinit var dialog: Dialog
    private var backPressedOnce = false
    private lateinit var mapView: MapView

    //create binding obj
    private lateinit var binding: ActivityCityDriveMainBinding
    private final val REQ_CODE = 1010


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCityDriveMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize MapView...
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {

            mapViewModel.initializeMapView(binding.mapView)

        }

        //viewModels Functions Call..
        locationViewModel.checkGPSStatus()
        mapViewModel.initializeMapView(binding.mapView)

        //observe GPS Status
        locationViewModel.isGPSEnabled.observe(this, Observer { isGPSEnabled ->
            if (isGPSEnabled == false)
            {
                showLocationSettingsDialog()
            }
            else
                Toast.makeText(this, "Gps is On", Toast.LENGTH_SHORT).show()
        })


        //Bottom Layouts Visibility controller...

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            binding.bottomSheet.height
            peekHeight = 480
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val intent = intent?.extras

        if (intent?.getBoolean("value") == true)
        {
           binding.cabBookingLayout.visibility = View.VISIBLE
            binding.bookingDetailsLayout.visibility = View.GONE
            binding.driverDerailsLayout.visibility = View.GONE
            val newHight = 370
            val newHightPx =  (newHight * resources.displayMetrics.density).toInt()

            val layoutParams = binding.bottomSheet.layoutParams
            layoutParams.height = newHightPx
            binding.bottomSheet.layoutParams = layoutParams
        }
        else if (intent?.getBoolean("loder") == true)
        {
            showDialog()

            binding.cabBookingLayout.visibility = View.GONE
            binding.bookingDetailsLayout.visibility = View.GONE
            binding.driverDerailsLayout.visibility = View.VISIBLE

            val newHight = 360
            val newHightPx =  (newHight * resources.displayMetrics.density).toInt()

            val layoutParams = binding.bottomSheet.layoutParams
            layoutParams.height = newHightPx
            binding.bottomSheet.layoutParams = layoutParams
        }
        else{
            binding.cabBookingLayout.visibility = View.GONE
            binding.driverDerailsLayout.visibility = View.GONE
            binding.bookingDetailsLayout.visibility = View.VISIBLE

            val newHight = 400
            val newHightPx =  (newHight * resources.displayMetrics.density).toInt()

            val layoutParams = binding.bottomSheet.layoutParams
            layoutParams.height = newHightPx
            binding.bottomSheet.layoutParams = layoutParams
        }


        //floating button click listener...

        binding.menuFloatingButton.setOnClickListener {
            binding.drawerLayout.open()
        }

        binding.locationFloatingButton.setOnClickListener {
            mapViewModel.initializeMapView(binding.mapView)
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
            startActivityForResult(Intent(this, LocationSearchActivity::class.java),REQ_CODE)
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



    private fun showDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialogbox)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        },3000)
    }




    override fun onResume() {
        super.onResume()
        binding.EtSearchLocation.isEnabled = true
        binding.carBook.isEnabled = true
        binding.autoBook.isEnabled = true
        binding.bikeBook.isEnabled = true
    }





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

    private fun openDialer(){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:8437837835")
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQ_CODE && resultCode == RESULT_OK){
//            Log.d("MapData",data?.getDoubleExtra("lat",0.0).toString())
//            Log.d("MapData",data?.getDoubleExtra("long",0.0).toString())
              val lat = data?.getDoubleExtra("lat",0.0)
              val lon = data?.getDoubleExtra("long",0.0)
            if (lat != null && lon != null) {
                addAnnotationToMap(lat,lon)
            }

        }

    }


    ////////////////////////////////////Adding Marker ////////////////////////////
    private fun addAnnotationToMap(lat:Double, lon:Double) {
// Create an instance of the Annotation API and get the PointAnnotationManager.
        binding.mapView.clearAnimation()
        bitmapFromDrawableRes(
            this@CityDriveMainActivity,
            R.drawable.location_search
        )?.let {
            val annotationApi = binding.mapView.annotations
            annotationApi.cleanup()
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager()
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(Point.fromLngLat(lon, lat))
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }


    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }





}