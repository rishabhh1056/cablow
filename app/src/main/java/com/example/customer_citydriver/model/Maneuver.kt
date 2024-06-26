package com.example.customer_citydriver.model

data class Maneuver(
    val bearing_after: Int,
    val bearing_before: Int,
    val instruction: String,
    val location: List<Double>,
    val modifier: String,
    val type: String
)