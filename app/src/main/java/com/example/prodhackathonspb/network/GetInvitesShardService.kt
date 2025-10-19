package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.GetMyInvitesResponse
import com.example.prodhackathonspb.network.models.GetMyInvitesShardResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface GetInvitesShardService {
    @GET("/shard_invite/get_my")
    suspend fun getInvites(@Header("Authorization") token: String): GetMyInvitesShardResponse
}