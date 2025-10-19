package com.example.prodhackathonspb.network

import com.example.prodhackathonspb.network.models.GetMyInvitesResponse
import com.example.prodhackathonspb.network.models.GroupInvite
import retrofit2.http.GET

interface GetInvitesService {
    @GET("/group_invite/get_my")
    suspend fun getInvites(): GetMyInvitesResponse
}