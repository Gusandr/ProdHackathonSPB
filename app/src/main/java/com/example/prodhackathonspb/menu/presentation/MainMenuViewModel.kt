package com.example.prodhackathonspb.menu.presentation

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
class MainMenuViewModel @Inject constructor(
    private val repository: Repository,
    private val tokenHolder: TokenHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainMenuUiState())
    val uiState: StateFlow<MainMenuUiState> = _uiState.asStateFlow()

    fun loadMainData() {
        viewModelScope.launch {
            try {
                val token = tokenHolder.getToken() ?: return@launch
                val user = repository.getUserService(token)
                val email = user.email ?: ""
                val username = email.substringBefore("@")
                _uiState.value = MainMenuUiState(userName = username)
            } catch (e: Exception) {
                _uiState.value = MainMenuUiState(error = "Ошибка: ${e.localizedMessage}")
            }
        }
    }

    // убедись, что метод в репозитории работает.
    suspend fun addGroup(): Boolean {
        return try {
            repository.addGroup()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = _uiState.value.copy(error = "Ошибка: ${e.localizedMessage}")
            false
        }
    }
}

data class MainMenuUiState(
    val userName: String = "",
    val error: String? = null
)
