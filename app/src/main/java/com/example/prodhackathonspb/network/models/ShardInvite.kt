package com.example.prodhackathonspb.network.models

data class ShardInvite(val shard: Shard, val inviter: User, val invitee: User, val active: Boolean)
