package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class GPU(val id: String, val usage: Float) {
}