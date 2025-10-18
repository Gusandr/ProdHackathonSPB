package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class ValidationDetail(
    val loc: List<String>,
    val msg: String,
    val type: String
)