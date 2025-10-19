package com.example.prodhackathonspb.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInviteBody(
    @SerialName("group_id") val groupId: String,
    @SerialName("invitee_id") val inviteeId: String
)