package com.example.prodhackathonspb.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.login.data.TokenHolder
import com.example.prodhackathonspb.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenHolder: TokenHolder,
    private val repository: Repository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            try {
                // Получаем токен
                val token = tokenHolder.getToken()

                if (token.isNullOrBlank()) {
                    // Токена нет - на экран входа
                    _authState.value = AuthState.Unauthenticated
                    return@launch
                }

                // Токен есть - проверяем его валидность
                val isNetworkAvailable = repository.checkStatus()

                if (!isNetworkAvailable) {
                    // Нет сети - доверяем локальному токену
                    _authState.value = AuthState.Authenticated
                    return@launch
                }

                // Проверяем токен на сервере
                try {
                    val user = repository.getUserService(token)

                    // Токен валидный - пользователь авторизован
                    _authState.value = AuthState.Authenticated

                } catch (e: Exception) {
                    // Токен невалидный - удаляем и на экран входа
                    tokenHolder.clearToken()
                    _authState.value = AuthState.Unauthenticated
                }

            } catch (e: Exception) {
                // Неожиданная ошибка
                _authState.value = AuthState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun retry() {
        _authState.value = AuthState.Loading
        checkAuthentication()
    }
}
