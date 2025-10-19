package com.example.prodhackathonspb.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Group(
    val id: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("admin_ids") val adminIds: List<String>,
    @SerialName("shard_ids") val shardIds: List<String>,
    @SerialName("gpu_pool_ids") val pool: List<String>
) {
}