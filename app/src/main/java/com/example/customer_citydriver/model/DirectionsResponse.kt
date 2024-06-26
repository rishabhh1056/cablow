package com.example.customer_citydriver.model

data class DirectionsResponse(
    val code: String,
    val routes: List<Route>,
    val uuid: String,
    val waypoints: List<Waypoint>
)