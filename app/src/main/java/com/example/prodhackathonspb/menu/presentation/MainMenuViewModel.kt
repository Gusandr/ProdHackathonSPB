package com.example.prodhackathonspb.main.presentation

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

data class MainMenuUiState(
    val userName: String = "",
    val error: String? = null
)

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val tokenHolder: TokenHolder,
    private val repository: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainMenuUiState())
    val uiState: StateFlow<MainMenuUiState> = _uiState.asStateFlow()

    init { loadMainData() }

    fun loadMainData() {
        viewModelScope.launch {
            try {
                // Получить и декодировать токен пользователя (или профиль)
                val token = tokenHolder.getToken()
                if (token == null) {
                    _uiState.value = MainMenuUiState(error = "Требуется авторизация")
                    return@launch
                }
                 val user = repository.getUserService(token)
                val email = user.email ?: ""
                val username = email.substringBefore("@")
                _uiState.value = MainMenuUiState(userName = username)
            } catch (e: Exception) {
                _uiState.value = MainMenuUiState(error = "Ошибка: ${e.localizedMessage}")
            }
        }
    }
}
