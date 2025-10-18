package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class Shard(val id: String, val owner: User, val members: List<User>) {
}