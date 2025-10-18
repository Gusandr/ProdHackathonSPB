package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class SignRequest(
    val email: String,
    val password: String
)