package com.example.prodhackathonspb.repository

import com.example.prodhackathonspb.network.GetUserService
import com.example.prodhackathonspb.network.ServerStatusService
import com.example.prodhackathonspb.network.SignInService
import com.example.prodhackathonspb.network.SignUpService
import com.example.prodhackathonspb.network.models.ApiResult
import com.example.prodhackathonspb.network.models.SignRequest
import com.example.prodhackathonspb.network.models.User
import com.example.prodhackathonspb.network.models.ValidationError
import kotlinx.serialization.json.Json
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
        }.fold(
            onSuccess = { true },
            onFailure = { false }
        )
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

    // ВХОД - sign_in
    suspend fun signIn(email: String, password: String): ApiResult<String> {
        return try {
            val response = signInService.signIn(SignRequest(email, password))

            when {
                response.isSuccessful -> {
                    val token = response.body()?.accessToken
                    if (token != null) {
                        ApiResult.Success(token)
                    } else {
                        ApiResult.Error(response.code(), "Empty response body")
                    }
                }
                response.code() == 404 -> {
                    ApiResult.Error(404, "Пользователь с таким email не зарегистрирован")
                }
                response.code() == 422 -> {
                    val errorBody = response.errorBody()?.string()
                    val message = parseValidationError(errorBody) ?: "Неверный email или пароль"
                    ApiResult.Error(422, message)
                }
                else -> {
                    ApiResult.Error(response.code(), response.message())
                }
            }
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }
    }

    // РЕГИСТРАЦИЯ - sign_up
    suspend fun signUp(email: String, password: String): ApiResult<String> {
        return try {
            val response = signUpService.signUp(SignRequest(email, password))

            when {
                response.isSuccessful -> {
                    val token = response.body()?.accessToken
                    if (token != null) {
                        ApiResult.Success(token)
                    } else {
                        ApiResult.Error(response.code(), "Empty response body")
                    }
                }
                response.code() == 409 -> {
                    ApiResult.Error(409, "Пользователь с таким email уже существует")
                }
                response.code() == 422 -> {
                    val errorBody = response.errorBody()?.string()
                    val message = parseValidationError(errorBody) ?: "Неверные данные"
                    ApiResult.Error(422, message)
                }
                else -> {
                    ApiResult.Error(response.code(), response.message())
                }
            }
        } catch (e: Throwable) {
            ApiResult.Exception(e)
        }
    }

    private fun parseValidationError(errorBody: String?): String? {
        return try {
            errorBody?.let {
                val json = Json { ignoreUnknownKeys = true }
                val error = json.decodeFromString<ValidationError>(it)
                error.detail.joinToString(", ") { detail -> detail.msg }
            }
        } catch (e: Exception) {
            null
        }
    }
}
