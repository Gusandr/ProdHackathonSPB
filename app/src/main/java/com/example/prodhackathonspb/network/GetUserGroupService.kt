package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.Group
import com.example.prodhackathonspb.network.models.User
import retrofit2.http.GET
import retrofit2.http.Header

interface GetUserGroupService {
    @GET("user_groups")
    suspend fun getUserGroup(
        @Header("Authorization") token: String
    ): List<Group>
}