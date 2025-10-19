package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class GetMyInvitesShardResponse(
    val invites: List<Shard>
)