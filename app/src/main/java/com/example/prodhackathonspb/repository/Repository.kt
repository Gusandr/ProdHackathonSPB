package com.example.prodhackathonspb.repository

import com.example.prodhackathonspb.network.GetUserService
import com.example.prodhackathonspb.network.ServerStatusService
import com.example.prodhackathonspb.network.SignInService
import com.example.prodhackathonspb.network.SignUpService
import com.example.prodhackathonspb.network.models.SignRequest
import com.example.prodhackathonspb.network.models.User
import javax.inject.Inject

class Repository @Inject constructor(
    private val service: ServerStatusService,
    private val getUserService: GetUserService,
    private val signUpService: SignUpService,
    private val signInService: SignInService,
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

    suspend fun getUserService(token: String): User {
        return runCatching {
            getUserService.getUser("Bearer $token")
        }.fold(
            onSuccess = { user -> user },
            onFailure = { exception ->
                throw Exception("Failed to fetch user: ${exception.message}", exception)
            }
        )
    }

    suspend fun signUp(email: String, password: String): String? {
        return runCatching {
            signUpService.signUp(SignRequest(email, password))
        }.fold(onSuccess = {
            it
        }, onFailure = { exception ->
            throw Exception("Failed to signUp user: ${exception.message}", exception)
        })
    }

    suspend fun signIn(email: String, password: String): String? {
        return runCatching {
            signInService.signIn(SignRequest(email, password))
        }.fold(onSuccess = {
            it
        }, onFailure = { exception ->
            throw Exception("Failed to signUp user: ${exception.message}", exception)
        })
    }
}