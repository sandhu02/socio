package com.example.cusotmauthtest.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusotmauthtest.retrofit.User
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import com.example.cusotmauthtest.retrofit.getUsername
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class UsersUiState {
    object Loading : UsersUiState()
    data class Success(val users: List<User>) : UsersUiState()
    data class Failure(val error: String) : UsersUiState()
}

class UsersForChatViewModel(val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow<UsersUiState>(UsersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = UsersUiState.Loading
            try {
                val currentUsername = getUsername(context)
                val allUsers = getAuthApiInstance(context)?.getUsers()
                if (allUsers == null) {
                    _uiState.value = UsersUiState.Failure("Failed to load users")
                } else {
                    // Filter out the logged-in user if available
                    val chatUsers = if (!currentUsername.isNullOrBlank()) {
                        allUsers.filter { it.username != currentUsername }
                    } else {
                        allUsers
                    }
                    _uiState.value = UsersUiState.Success(chatUsers)
                }
            } catch (e: Exception) {
                _uiState.value = UsersUiState.Failure("Error fetching users: ${e.localizedMessage}")
            }
        }
    }
}
