package com.example.prodhackathonspb.login.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.login.data.TokenHolder
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
    private val tokenManager: TokenHolder
) : ViewModel() {

    private val _showNetworkError = MutableSharedFlow<String>()
    val showNetworkError: SharedFlow<String> = _showNetworkError.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<Unit>()
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.asSharedFlow()

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Проверяем сеть перед запросом
                if (!repository.checkStatus()) {
                    _showNetworkError.emit("Проверьте подключение к интернету")
                    return@launch
                }

            } catch (e: Exception) {
                _showNetworkError.emit("Ошибка регистрации: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
