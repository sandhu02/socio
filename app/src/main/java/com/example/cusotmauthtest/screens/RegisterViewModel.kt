package com.example.cusotmauthtest.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusotmauthtest.retrofit.Failure
import com.example.cusotmauthtest.retrofit.Loading
import com.example.cusotmauthtest.retrofit.RegisterRequest
import com.example.cusotmauthtest.retrofit.Response
import com.example.cusotmauthtest.retrofit.Success
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val username: String = "",
    val name: String = "",
    val password: String = "",
    val response: Response? = null
)

class RegisterViewModel(val context: Context) : ViewModel() {
    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState = _registerUiState.asStateFlow()

    fun updateEmail(newEmail: String) {
        _registerUiState.update { 
            it.copy(
                email = newEmail,
                name = if (it.name.isBlank()) newEmail.substringBefore("@") else it.name
            ) 
        }
    }

    fun updateUsername(newUsername: String) {
        // Strip leading @ if entered
        val clean = if (newUsername.startsWith("@")) newUsername.removePrefix("@") else newUsername
        _registerUiState.update { it.copy(username = clean) }
    }

    fun updateName(newName: String) {
        _registerUiState.update { it.copy(name = newName) }
    }

    fun updatePassword(newPassword: String) {
        _registerUiState.update { it.copy(password = newPassword) }
    }

    fun register() {
        val currentState = _registerUiState.value
        val effectiveName = currentState.name.ifBlank {
            currentState.email.substringBefore("@").ifBlank { currentState.username }
        }
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _registerUiState.update { it.copy(response = Failure("Please fill in all fields")) }
            return
        }

        val registerRequest = RegisterRequest(
            username = currentState.username.trim(),
            name = effectiveName.trim(),
            password = currentState.password,
        )

        viewModelScope.launch {
            _registerUiState.update { it.copy(response = Loading) }
            try {
                val response = getAuthApiInstance(context = context)?.register(registerRequest)
                if (response == null) {
                    _registerUiState.update { it.copy(response = Failure("Empty Response Body")) }
                } else if (response.success) {
                    _registerUiState.update {
                        it.copy(response = Success(response.message))
                    }
                } else {
                    _registerUiState.update { it.copy(response = Failure("Register Failed: ${response.message}")) }
                }
            } catch (e: Exception) {
                _registerUiState.update { it.copy(response = Failure("Network error: ${e.localizedMessage}")) }
            }
        }
    }
}