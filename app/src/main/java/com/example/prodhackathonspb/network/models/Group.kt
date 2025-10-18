package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class Group(val id: String, val owner: User, val admins: List<User>, val shards: List<Shard>, val pool: List<GPU>) {
}