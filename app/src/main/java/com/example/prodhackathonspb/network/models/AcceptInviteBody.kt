package com.example.prodhackathonspb.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AcceptInviteBody(
    @SerialName("invite_id") val inviteId: String
)
