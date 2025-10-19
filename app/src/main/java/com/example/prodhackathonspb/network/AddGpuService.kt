package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.GPU
import retrofit2.http.Header
import retrofit2.http.POST

interface AddGpuService {
    @POST("gpu/add_gpu")
    suspend fun addGpu(@Header("Authorization") token: String): GPU
}