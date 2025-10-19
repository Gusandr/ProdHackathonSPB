package com.example.prodhackathonspb.groups.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prodhackathonspb.network.models.Group
import com.example.prodhackathonspb.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityGroupsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    init { loadGroups() }

    fun loadGroups() {
        viewModelScope.launch {
            try {
                val groups = repository.getMyGroups()
                _groups.value = groups
            } catch (e: Exception) {
                _errorFlow.emit("Ошибка загрузки групп: ${e.localizedMessage}")
            }
        }
    }

    fun addGroup() {
        try {
            viewModelScope.launch {
                repository.addGroup()
            }

        } catch (e: Exception) {

        }
    }
}
