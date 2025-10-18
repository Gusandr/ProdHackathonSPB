package com.example.prodhackathonspb.repository

import com.example.prodhackathonspb.network.GetUserService
import com.example.prodhackathonspb.network.ServerStatusService
import com.example.prodhackathonspb.network.SignUpService
import com.example.prodhackathonspb.network.models.SignUpRequest
import com.example.prodhackathonspb.network.models.User
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ServerStatusService,
    private val getUserService: GetUserService,
    private val signUpService: SignUpService,
) {
    suspend fun checkStatus(): Boolean {
        return runCatching {
            service.getStatus()
        }.fold(onSuccess = {
            true
        }, onFailure = {
            false
        })
    }

    suspend fun getUserService(token: String = "gAAAAABo82H6l3WlrpBtuYb315V4o5QwPTIogsQGRWsIAEk68WCsUfV93ABA3rFomOQG67nYvAH2_ThfA67n6r6P_bIc3PnoaA2j1hugotPpg_DzF8_gue-_FIGqFtoAYqaEEy2v-pBs"): User {
        return runCatching {
            getUserService.getUser("Bearer $token")
        }.fold(
            onSuccess = { user -> user },
            onFailure = { exception ->
                throw Exception("Failed to fetch user: ${exception.message}", exception)
            }
        )
    }

    suspend fun signUp(email: String, password: String): String {
        return runCatching {
            signUpService.signUp(SignUpRequest(email, password))
        }.fold(onSuccess = {
            it
        }, onFailure = { exception ->
            throw Exception("Failed to signUp user: ${exception.message}", exception)
        })
    }
}