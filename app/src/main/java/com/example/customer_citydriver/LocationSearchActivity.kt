package com.example.customer_citydriver

/*
//import com.mapbox.search.autofill.Query
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import androidx.activity.enableEdgeToEdge
//import androidx.activity.viewModels
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.isVisible
//import androidx.core.widget.addTextChangedListener
//import androidx.lifecycle.lifecycleScope
//import com.example.customer_citydriver.Views.HomeScreen.LocationViewModel
//import com.example.customer_citydriver.adapter.FatchLocationAdapter
//import com.example.customer_citydriver.databinding.ActivityLocationSearchBinding
//import com.example.customer_citydriver.retrofit.retrofitClient
//import com.mapbox.bindgen.Expected
//import com.mapbox.geojson.Point
//import com.mapbox.search.autofill.AddressAutofill
//import com.mapbox.search.autofill.AddressAutofillOptions
//import com.mapbox.search.autofill.AddressAutofillResult
//import com.mapbox.search.autofill.AddressAutofillSuggestion
//import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
//import com.mapbox.search.ui.view.SearchResultsView
//import kotlinx.coroutines.launch
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class LocationSearchActivity : AppCompatActivity() {
//
//
//    companion object {
//        const val REQUEST_LOCATION_PERMISSION = 1
//    }
//    private lateinit var adapter: FatchLocationAdapter
//    private lateinit var sharedPreferences: SharedPreferences
//    private val locationViewModel : LocationViewModel by viewModels()
//
//
//    private lateinit var addressAutofill: AddressAutofill
//
//    private lateinit var searchResultsView: SearchResultsView
//    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter
//
//    private var ignoreNextMapIdleEvent: Boolean = false
//    private var ignoreNextQueryTextUpdate: Boolean = false
//
//
//
//    private lateinit var binding: ActivityLocationSearchBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//
//       /* addressAutofill = AddressAutofill.create()
//
//
//        lifecycleScope.launchWhenCreated {
//
//
//            val query = Query.create(binding.EtPickUpLocation.text.toString()) ?: return@launchWhenCreated
//
//            val response = addressAutofill.suggestions(
//                query = query,
//                options = AddressAutofillOptions()
//            )
//
//            if (response.isValue) {
//                val suggestions = requireNotNull(response.value)
//                Log.e("SearchApiExample", "Autofill suggestions: $suggestions")
//
//                if (suggestions.isNotEmpty()) {
//                    // Supposing that a user has selected (clicked in UI) the first suggestion
//                    val selectedSuggestion = suggestions.first()
//
//                    Log.e("SearchApiExample", "Selecting first suggestion...")
//
//                    val selectionResponse = addressAutofill.select(selectedSuggestion)
//                    selectionResponse.onValue { result ->
//                        Log.e("SearchApiExample", "Autofill result: $result")
//                    }.onError { e ->
//                        Log.e("SearchApiExample", "An error occurred during selection", e)
//                    }
//                }
//            }
//            else {
//                Log.e("SearchApiExample", "Autofill suggestions error", response.error)
//            }
//        }*/
//
//        addressAutofill = AddressAutofill.create()
//
//        binding.EtPickUpLocation.addTextChangedListener{ editable ->
//
//            val query = Query.create(editable.toString()) ?: return@addTextChangedListener
//
//            lifecycleScope.launch{
//                val response = addressAutofill.suggestions(
//                    query = query,
//                    options = AddressAutofillOptions()
//                )
//                if (response.isValue) {
//                    val suggestions = requireNotNull(response.value)
//                    Log.d("SearchApiExample", "Autofill suggestions: $suggestions")
//
//                    if (suggestions.isNotEmpty()) {
//                        // Supposing that a user has selected (clicked in UI) the first suggestion
//                        val selectedSuggestion = suggestions.first()
//
////                        Log.d("SearchApiExample", "Selecting first suggestion...")
//
//                        val selectionResponse = addressAutofill.select(selectedSuggestion)
//
//                        selectionResponse.onValue { result ->
//                            Log.d("SearchApiExample", "Autofill result: $result")
//                        }.onError { e ->
//                            Log.d("SearchApiExample", "An error occurred during selection", e)
//                        }
//                    }
//                    else{
//                        binding.EtPickUpLocation.setText("Your Current location")
//                    }
//                }
//                else {
//                    Log.d("SearchApiExample", "Autofill suggestions error", response.error)
//                }
//            }
//        }
//
//
//
//       /* binding.EtPickUpLocation.addTextChangedListener(object : TextWatcher{
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//                if(ignoreNextQueryTextUpdate)
//                {
//                    ignoreNextQueryTextUpdate = false
//                    return
//                }
//                val query = Query.create(s.toString()) ?: return
//
//                lifecycleScope.launchWhenCreated {
//
//                    val response = addressAutofillUiAdapter.search(query)
//
//                    searchResultsView.isVisible = true
//                }
//
//
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                TODO("Not yet implemented")
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
//            }
//
//        })*/
//
//
//
//
//
//        addressAutofillUiAdapter = AddressAutofillUiAdapter(binding.searchResultsView, addressAutofill)
//
//        addressAutofillUiAdapter.addSearchListener(object : AddressAutofillUiAdapter.SearchListener {
//            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
//                updateUIWithSuggestion(suggestion)
//            }
//
//            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
//                // Optionally handle showing suggestions in the UI if needed
//                Log.d("SearchApiExample", "onSuggestionsShown: $suggestions", )
//            }
//
//            override fun onError(e: Exception) {
//                // Handle error if necessary
//                Log.e("TAG", "onError: $e", )
//            }
//        })
//
//
//
//        binding.EtPickUpLocation.setOnClickListener {
//            val value = binding.EtPickUpLocation.text.toString()
//            if (value == "Your Current location") {
//                binding.EtPickUpLocation.setText(null)
//            }
//
//
//        }
//
//
//        binding.backBtn.setNavigationOnClickListener {
//            val intent  = Intent(this, CityDriveMainActivity::class.java)
//            val bundle = Bundle()
//            bundle.putBoolean("value", true)
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//
//
////            val intent  = Intent(this, CityDriveMainActivity::class.java)
////            val bundle = Bundle()
////            bundle.putBoolean("value", true)
////            intent.putExtras(bundle)
////            startActivity(intent)
////            finish()
//
//
//        }
//
//    private fun updateUIWithSuggestion(suggestion: AddressAutofillSuggestion) {
//        // Implement this method to update your UI with the selected suggestion
//        // Example: Update EditText fields with suggestion details
//        binding.EtPickUpLocation.setText(suggestion.formattedAddress)
//        binding.EtPickUpLocation.clearFocus()
//        searchResultsView.isVisible = false // Hide search results view
//    }
//
//
//
//    }
//
//
//
//
//  /*  private fun searchLocation(){
//
//        val retrofit = retrofitClient.retrofit
//
//        retrofit.searchLocation("Ghazipur").enqueue(object : Callback<requestSuggestion>{
//            override fun onResponse(call: Call<requestSuggestion>, response: Response<requestSuggestion>) {
//
//                if (response.isSuccessful)
//                {
//                    val data = response.body()?.suggestions
//                    Log.e("TAG", "onResponse: $data ", )
//                }
//                else
//                {
//                    Log.e("TAG", "onResponse: else part ", )
//                }
//            }
//
//            override fun onFailure(call: Call<requestSuggestion>, t: Throwable) {
//                Log.e("TAG", "onFailure: ${t.message}", )
//            }
//
//        })
//    }*/
//
////    fun getLocation(): android.location.Location? {
////
////        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
////        if (ActivityCompat.checkSelfPermission(
////                this,
////                Manifest.permission.ACCESS_FINE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
////                this,
////                Manifest.permission.ACCESS_COARSE_LOCATION
////            ) != PackageManager.PERMISSION_GRANTED
////        )
////
////
////            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
////
////
////        return null
////    }
//
//
*/

////////////////////////////////////////////////////////////////////////////////////////////////////////


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
//import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.search.autofill.AddressAutofill
import com.mapbox.search.autofill.AddressAutofillResult
import com.mapbox.search.autofill.AddressAutofillSuggestion
import com.mapbox.search.autofill.Query
import com.mapbox.search.ui.adapter.autofill.AddressAutofillUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchResultsView

class LocationSearchActivity : AppCompatActivity() {

    private lateinit var addressAutofill: AddressAutofill

    private lateinit var searchResultsView: SearchResultsView
    private lateinit var addressAutofillUiAdapter: AddressAutofillUiAdapter

    private lateinit var queryEditText: EditText

    private lateinit var apartmentEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var zipEditText: EditText
    private lateinit var fullAddress: TextView
    private lateinit var pinCorrectionNote: TextView
    private lateinit var mapView: MapView
    private lateinit var mapPin: View
    private lateinit var mapboxMap: MapboxMap

    private var ignoreNextMapIdleEvent: Boolean = false
    private var ignoreNextQueryTextUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_search)

        addressAutofill = AddressAutofill.create()

        queryEditText = findViewById(R.id.query_text)
        apartmentEditText = findViewById(R.id.address_apartment)
        cityEditText = findViewById(R.id.address_city)
        stateEditText = findViewById(R.id.address_state)
        zipEditText = findViewById(R.id.address_zip)
        fullAddress = findViewById(R.id.full_address)
        pinCorrectionNote = findViewById(R.id.pin_correction_note)

        queryEditText.requestFocus()

        mapPin = findViewById(R.id.map_pin)
        mapView = findViewById(R.id.map)
        mapboxMap = mapView.getMapboxMap()
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)
        mapboxMap.addOnMapIdleListener {
            if (ignoreNextMapIdleEvent) {
                ignoreNextMapIdleEvent = false
                return@addOnMapIdleListener
            }

//            val mapCenter = mapboxMap.cameraState.center
//            findAddress(mapCenter)
        }

        searchResultsView = findViewById(R.id.search_results_view)

        searchResultsView.initialize(
            SearchResultsView.Configuration(
                commonConfiguration = CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL)
            )
        )

        addressAutofillUiAdapter = AddressAutofillUiAdapter(
            view = searchResultsView,
            addressAutofill = addressAutofill
        )

//        LocationEngineProvider.getBestLocationEngine(applicationContext).lastKnownLocation(this) { point ->
//            point?.let {
//                mapView.getMapboxMap().setCamera(
//                    CameraOptions.Builder()
//                        .center(point)
//                        .zoom(9.0)
//                        .build()
//                )
//                ignoreNextMapIdleEvent = true
//            }
//        }


        addressAutofillUiAdapter.addSearchListener(object : AddressAutofillUiAdapter.SearchListener {

            override fun onSuggestionSelected(suggestion: AddressAutofillSuggestion) {
                selectSuggestion(
                    suggestion,
                    fromReverseGeocoding = false,
                )
            }

            override fun onSuggestionsShown(suggestions: List<AddressAutofillSuggestion>) {
                // Nothing to do
            }

            override fun onError(e: Exception) {
                // Nothing to do
            }
        })

        queryEditText.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (ignoreNextQueryTextUpdate) {
                    ignoreNextQueryTextUpdate = false
                    return
                }

                val query = Query.create(text.toString())
                if (query != null) {
                    lifecycleScope.launchWhenStarted {
                        addressAutofillUiAdapter.search(query)
                    }
                }
                searchResultsView.isVisible = query != null
            }





            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Nothing to do
            }

            override fun afterTextChanged(s: Editable) {
                // Nothing to do
            }
        })

        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSIONS_REQUEST_LOCATION
            )
        }
    }


    private fun isPermissionGranted(fineLocation: String): Boolean {
        return (ActivityCompat.checkSelfPermission
        (this@LocationSearchActivity,fineLocation) == PackageManager.PERMISSION_GRANTED)
    }

//    private fun findAddress(point: Point) {
//        lifecycleScope.launchWhenStarted {
//            val response = addressAutofill.suggestions(point, AddressAutofillOptions())
//
//            response.onValue { suggestions ->
//                if (suggestions.isEmpty()) {
//                    showToast(R.string.address_autofill_error_pin_correction)
//                } else {
//                    selectSuggestion(
//                        suggestions.first(),
//                        fromReverseGeocoding = true
//                    )
//                }
//            }.onError {
//                showToast(R.string.address_autofill_error_pin_correction)
//            }
//        }
//    }

    private fun selectSuggestion(suggestion: AddressAutofillSuggestion, fromReverseGeocoding: Boolean) {
        lifecycleScope.launchWhenStarted {
            val response = addressAutofill.select(suggestion)
            response.onValue { result ->
                showAddressAutofillResult(result, fromReverseGeocoding)
            }.onError {
//                showToast(R.string.address_autofill_error_select)
                Toast.makeText(this@LocationSearchActivity,"Error",Toast.LENGTH_LONG).show()
            }
        }
    }

    // This Function is used to Extract the data from selected Result and Set to the Views
    private fun showAddressAutofillResult(result: AddressAutofillResult, fromReverseGeocoding: Boolean) {
        val address = result.address
        cityEditText.setText(address.place)
        stateEditText.setText(address.region)
        zipEditText.setText(address.postcode)

        fullAddress.isVisible = true
        fullAddress.text = result.suggestion.formattedAddress
        //result.suggestion.coordinate.longitude()

        pinCorrectionNote.isVisible = true

        if (!fromReverseGeocoding) {
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(result.suggestion.coordinate)
                    .zoom(16.0)
                    .build()
            )
            ignoreNextMapIdleEvent = true
            mapPin.isVisible = true
        }

        ignoreNextQueryTextUpdate = true
        queryEditText.setText(
//            listOfNotNull(
//                address.houseNumber,
//                address.street
//            ).joinToString()
            result.suggestion.formattedAddress
        )
        queryEditText.clearFocus()
        setCoordinatesForMap(result.coordinate.latitude(),result.coordinate.longitude())

        searchResultsView.isVisible = false
//        searchResultsView.hideKeyboard()
    }

    private fun setCoordinatesForMap(latitude: Double, longitude: Double) {
        val data = Intent()
        data.putExtra("lat",latitude)
        data.putExtra("long",longitude)

        setResult(RESULT_OK,data)
        finish()
    }

    private companion object {
        const val PERMISSIONS_REQUEST_LOCATION = 0
    }

}
