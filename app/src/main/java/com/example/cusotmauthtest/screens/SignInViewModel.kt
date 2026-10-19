package com.example.cusotmauthtest.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusotmauthtest.retrofit.Failure
import com.example.cusotmauthtest.retrofit.Loading
import com.example.cusotmauthtest.retrofit.LoginRequest
import com.example.cusotmauthtest.retrofit.RegisterNotificationRequest
import com.example.cusotmauthtest.retrofit.Response
import com.example.cusotmauthtest.retrofit.Success
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import com.example.cusotmauthtest.retrofit.getBaseUrl
import com.example.cusotmauthtest.retrofit.getToken
import com.example.cusotmauthtest.retrofit.saveFcmToken
import com.example.cusotmauthtest.retrofit.saveToken
import com.example.cusotmauthtest.retrofit.saveUsername
import com.example.cusotmauthtest.retrofit.setBaseUrl
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class SignInUiState(
    val username: String = "",
    val password: String = "",
    val serverUrl: String = "",
    val response: Response? = null,
    val localToken: String? = null
)

class SignInViewModel(val context: Context) : ViewModel() {
    private val _signInUiState = MutableStateFlow(
        SignInUiState(
            serverUrl = getBaseUrl(context),
            localToken = getToken(context)
        )
    )
    val signInUiState = _signInUiState.asStateFlow()

    fun updateUsername(newUsername: String) {
        _signInUiState.update { it.copy(username = newUsername) }
    }

    fun updatePassword(newPassword: String) {
        _signInUiState.update { it.copy(password = newPassword) }
    }

    fun updateServerUrl(url: String) {
        _signInUiState.update { it.copy(serverUrl = url) }
        setBaseUrl(context, url)
    }

    fun login() {
        val currentState = _signInUiState.value
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _signInUiState.update { it.copy(response = Failure("Please enter both username and password")) }
            return
        }

        viewModelScope.launch {
            _signInUiState.update { it.copy(response = Loading) }
            try {
                kotlinx.coroutines.withTimeout(6000L) {
                    val api = getAuthApiInstance(context = context)
                    val response = api?.login(
                        LoginRequest(
                            username = currentState.username.trim(),
                            password = currentState.password
                        )
                    )

                    if (response == null) {
                        _signInUiState.update { it.copy(response = Failure("Empty response from server")) }
                    } else if (response.success) {
                        saveToken(context, response.token)
                        saveUsername(context, currentState.username.trim())

                        try {
                            val fcmToken = FirebaseMessaging.getInstance().token.await()
                            saveFcmToken(context, fcmToken)
                            api.registerNotification(
                                RegisterNotificationRequest(
                                    username = currentState.username.trim(),
                                    fcmtoken = fcmToken
                                )
                            )
                        } catch (e: Exception) {
                            Log.w("SignInViewModel", "FCM token registration failed: ${e.message}")
                        }

                        _signInUiState.update {
                            it.copy(
                                response = Success(response.message),
                                localToken = response.token
                            )
                        }
                    } else {
                        _signInUiState.update { it.copy(response = Failure(response.message)) }
                    }
                }
            } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                _signInUiState.update { it.copy(response = Failure("Server connection timed out")) }
            } catch (e: Exception) {
                _signInUiState.update { it.copy(response = Failure("Unable to connect to server")) }
            }
        }
    }

    fun getFirebaseToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                saveFcmToken(context, token)
                val username = _signInUiState.value.username.ifBlank { "anonymous" }
                getAuthApiInstance(context)?.registerNotification(
                    RegisterNotificationRequest(
                        username = username,
                        fcmtoken = token
                    )
                )
                _signInUiState.update { it.copy(localToken = token) }
            } catch (e: Exception) {
                _signInUiState.update { it.copy(response = Failure("Failed to get FCM token: ${e.localizedMessage}")) }
            }
        }
    }
}
