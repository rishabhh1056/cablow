package com.example.customer_citydriver.model

data class Step(
    val destinations: String,
    val distance: Double,
    val driving_side: String,
    val duration: Double,
    val exits: String,
    val geometry: Geometry,
    val intersections: List<Intersection>,
    val maneuver: Maneuver,
    val mode: String,
    val name: String,
    val ref: String,
    val weight: Double
)