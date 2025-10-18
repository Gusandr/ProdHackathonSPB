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
    private val _authState = MutableStateFlow<SplashState>(SplashState.Loading)
    val authState: StateFlow<SplashState> = _authState.asStateFlow()

    init { checkAuth() }

    fun checkAuth() {
        viewModelScope.launch {
            val token = tokenHolder.getToken()
            if (token.isNullOrBlank()) {
                _authState.value = SplashState.Unauthenticated
                return@launch
            }
            // можно также проверить бэкенд-валидность токена: try { repo.getUserService(token)... }
            _authState.value = SplashState.Authenticated
        }
    }
}

sealed class SplashState {
    object Loading : SplashState()
    object Authenticated : SplashState()
    object Unauthenticated : SplashState()
    data class Error(val msg: String) : SplashState()
}
