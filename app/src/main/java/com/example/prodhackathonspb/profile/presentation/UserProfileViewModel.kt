package com.example.prodhackathonspb.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.login.data.TokenHolder
import com.example.prodhackathonspb.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUiState(
    val email: String? = null,
    val avatarUrl: String? = null, // если нужен внешний аватар
    val hasInvite: Boolean = false,
    val inviteFrom: String? = null,
    val inviteGroup: String? = null
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val tokenHolder: TokenHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val _logoutFlow = MutableSharedFlow<Unit>()
    val logoutFlow = _logoutFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            try {
                val token = tokenHolder.getToken()
                if (token == null) {
                    _errorFlow.emit("Ошибка авторизации")
                    return@launch
                }
                val user = repository.getUserService(token)
                // имена полей корректируй по своей модели User!
                _uiState.value = UserProfileUiState(
                    email = user.email,
                    // avatarUrl = user.avatarUrl,
                    // тестово Демка приглашения
                    hasInvite = true, // сделай условие по своему бэку если реально есть приглашение
                    inviteFrom = "Вася Пупкин",
                    inviteGroup = "Группа 1"
                )
            } catch (e: Exception) {
                _errorFlow.emit("Ошибка загрузки профиля: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenHolder.clearToken()
            _logoutFlow.emit(Unit)
        }
    }

    fun onInviteAccepted() {
        viewModelScope.launch {
            // Тут запрос к бэку принять приглашение (по ID) и обновление uiState
            _errorFlow.emit("Группа принята (demo). Имплементируй реальный запрос.")
        }
    }

    fun onInviteDeclined() {
        viewModelScope.launch {
            // Тут запрос к бэку отклонить приглашение (по ID) и обновление uiState
            _errorFlow.emit("Приглашение отклонено (demo). Имплементируй реальный запрос.")
        }
    }
}
