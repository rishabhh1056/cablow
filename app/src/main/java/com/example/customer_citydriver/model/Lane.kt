package com.example.customer_citydriver.model

data class Lane(
    val active: Boolean,
    val indications: List<String>,
    val valid: Boolean,
    val valid_indication: String
)