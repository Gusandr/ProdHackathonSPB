package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class ValidationError(
    val detail: List<ValidationDetail>
)