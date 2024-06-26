package com.example.customer_citydriver.model

data class Intersection(
    val admin_index: Int,
    val bearings: List<Int>,
    val classes: List<String>,
    val duration: Double,
    val entry: List<Boolean>,
    val geometry_index: Int,
    val `in`: Int,
    val is_urban: Boolean,
    val lanes: List<Lane>,
    val location: List<Double>,
    val mapbox_streets_v8: MapboxStreetsV8,
    val `out`: Int,
    val stop_sign: Boolean,
    val toll_collection: TollCollection,
    val traffic_signal: Boolean,
    val turn_duration: Double,
    val turn_weight: Double,
    val weight: Double
)