package com.example.prodhackathonspb.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.login.data.TokenHolder
import com.example.prodhackathonspb.network.models.ApiResult
import com.example.prodhackathonspb.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    private val tokenHolder: TokenHolder
) : ViewModel() {

    private val _showNetworkError = MutableSharedFlow<String>()
    val showNetworkError: SharedFlow<String> = _showNetworkError.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Unit>()
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.asSharedFlow()

    // ВХОД - используется в LoginActivity
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Проверяем сеть
                if (!repository.checkStatus()) {
                    _showNetworkError.emit("Проверьте подключение к интернету")
                    return@launch
                }

                when (val result = repository.signIn(email, password)) {
                    is ApiResult.Success -> {
                        tokenHolder.saveToken(result.data)
                        _loginSuccess.emit(Unit)
                    }
                    is ApiResult.Error -> {
                        val message = when (result.code) {
                            404 -> "Пользователь с таким email не найден"
                            422 -> "Неверный email или пароль"
                            else -> "Ошибка: ${result.message}"
                        }
                        _showNetworkError.emit(message)
                    }
                    is ApiResult.Exception -> {
                        _showNetworkError.emit("Ошибка сети: ${result.throwable.message}")
                    }
                }
            } catch (e: Exception) {
                _showNetworkError.emit("Неизвестная ошибка: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // РЕГИСТРАЦИЯ - будет использоваться в SignUpActivity
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Проверяем сеть
                if (!repository.checkStatus()) {
                    _showNetworkError.emit("Проверьте подключение к интернету")
                    return@launch
                }

                when (val result = repository.signUp(email, password)) {
                    is ApiResult.Success -> {
                        tokenHolder.saveToken(result.data)
                        _loginSuccess.emit(Unit)
                    }
                    is ApiResult.Error -> {
                        val message = when (result.code) {
                            409 -> "Этот email уже зарегистрирован"
                            422 -> "Проверьте правильность данных"
                            else -> "Ошибка: ${result.message}"
                        }
                        _showNetworkError.emit(message)
                    }
                    is ApiResult.Exception -> {
                        _showNetworkError.emit("Ошибка сети: ${result.throwable.message}")
                    }
                }
            } catch (e: Exception) {
                _showNetworkError.emit("Неизвестная ошибка: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
