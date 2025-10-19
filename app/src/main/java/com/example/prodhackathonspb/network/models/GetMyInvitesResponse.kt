package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class GetMyInvitesResponse(
    val invites: List<GroupInvite>
)