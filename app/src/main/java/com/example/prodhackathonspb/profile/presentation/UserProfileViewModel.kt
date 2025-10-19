package com.example.prodhackathonspb.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.login.data.TokenHolder
import com.example.prodhackathonspb.network.models.GroupInvite
import com.example.prodhackathonspb.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUiState(
    val email: String? = null,
    val invites: List<GroupInvite> = emptyList()
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

    init { loadUser() }

    fun loadUser() {
        viewModelScope.launch {
            try {
                val token = tokenHolder.getToken()
                if (token == null) {
                    _errorFlow.emit("Ошибка авторизации")
                    return@launch
                }
                val user = repository.getUserService(token)
                val invites = repository.getMyInvites()
                _uiState.value = UserProfileUiState(
                    email = user.email,
                    invites = invites
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
    fun acceptInvite(inviteId: String) {
        viewModelScope.launch {
            val ok = repository.acceptInvite(inviteId)
            if (ok) {
                _uiState.value = _uiState.value.copy(
                    invites = _uiState.value.invites.filterNot { it.id == inviteId }
                )
            } else {
                _errorFlow.emit("Ошибка принятия приглашения")
            }
        }
    }
    fun declineInvite(inviteId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                invites = _uiState.value.invites.filterNot { it.id == inviteId }
            )
        }
    }
}
