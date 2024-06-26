package com.example.customer_citydriver.Views.HomeScreen

import android.provider.Settings.Global.getString
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.customer_citydriver.R
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import retrofit2.Call
import retrofit2.Response

class GetRoute {

    fun FoundRoute (origin: Point, destination: Point, accesToken: String) : MutableLiveData<DirectionsRoute> {

        val _mutableData = MutableLiveData<DirectionsRoute>()
        val data  = _mutableData

        val client = MapboxDirections.builder()
            .origin(origin)
            .destination(destination)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(accesToken)
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


        return data
    }

}