package com.example.prodhackathonspb.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupInvite(
    val id: String,
    @SerialName("group_id") val groupId: String,
    @SerialName("inviter_id") val inviterId: String,
    @SerialName("invitee_id") val inviteeId: String,
    val active: Boolean
)