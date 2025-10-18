package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class GroupInvite(val group: Group, val inviter: User, val invitee: User, val active: Boolean)