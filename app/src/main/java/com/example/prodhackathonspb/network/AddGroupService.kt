package com.example.prodhackathonspb.network

import retrofit2.http.Header
import retrofit2.http.POST

interface AddGroupService {
    @POST("group/add_group")
    suspend fun addGroup(@Header("Authorization") token: String): Unit
}