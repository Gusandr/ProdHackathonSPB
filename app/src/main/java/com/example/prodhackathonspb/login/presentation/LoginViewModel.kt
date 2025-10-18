package com.example.prodhackathonspb.login.presentation

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _showNetworkError = MutableSharedFlow<String>()
    val showNetworkError: SharedFlow<String> = _showNetworkError

    init {
        viewModelScope.launch {
            val status = repository.checkStatus()
            if (!status) {
                _showNetworkError.emit("Бля чел, что за хуйня, почини инет плиз")
            }
        }
    }
}