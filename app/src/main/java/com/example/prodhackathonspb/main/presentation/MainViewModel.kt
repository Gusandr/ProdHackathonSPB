package com.example.prodhackathonspb.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.login.data.TokenHolder
import com.example.prodhackathonspb.network.models.User
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
class MainViewModel @Inject constructor(
    private val repository: Repository,
    private val tokenHolder: TokenHolder
) : ViewModel() {

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent: SharedFlow<Unit> = _logoutEvent.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    fun loadUserData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Просто получить токен — он ДОЛЖЕН быть валиден на этом этапе!
                val token = tokenHolder.getToken()
                if (token == null) {
                    // Означает баг в flow приложения — Splash недоработан, но можно сразу логаутить
                    _logoutEvent.emit(Unit)
                    return@launch
                }

                val user = repository.getUserService(token)
                _userData.value = user

            } catch (e: Exception) {
                _errorEvent.emit("Ошибка загрузки данных: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
