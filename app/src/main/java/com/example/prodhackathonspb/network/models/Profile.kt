package com.example.prodhackathonspb.network.models

import kotlinx.serialization.Serializable

@Serializable
data class Profile(val user: User, val name: String) {

}
