package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.GPU
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupGpuAddService {
    @PATCH("group/{group_id}/gpu/add")
    suspend fun addGpu(@Header("Authorization") token: String, @Path("group_id") groupId: String, @Body groupBody: GroupBody): GPU

    @PATCH("group/{group_id}/gpu/add")
    suspend fun deleteGpu(@Header("Authorization") token: String, @Path("group_id") groupId: String, @Body groupBody: GroupBody): GPU
}

@Serializable
data class GroupBody(
    @SerialName("gpu_id") val gpuId: String
)