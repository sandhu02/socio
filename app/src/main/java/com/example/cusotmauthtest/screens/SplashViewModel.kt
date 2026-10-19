package com.example.cusotmauthtest.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusotmauthtest.retrofit.clearAuth
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import com.example.cusotmauthtest.retrofit.getToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlinx.coroutines.withTimeoutOrNull

sealed class SplashDestination {
    object Loading : SplashDestination()
    object Home : SplashDestination()
    object SignIn : SplashDestination()
}

class SplashViewModel(val context: Context) : ViewModel() {
    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Loading)
    val destination = _destination.asStateFlow()

    init {
        checkAuth()
    }

    fun checkAuth() {
        viewModelScope.launch {
            val token = getToken(context)
            if (token.isNullOrBlank()) {
                delay(400)
                _destination.value = SplashDestination.SignIn
                return@launch
            }

            try {
                // Maximum 2.5s wait to verify token; if server is offline, immediately fall back to SignIn
                val response = withTimeoutOrNull(2500L) {
                    val api = getAuthApiInstance(context)
                    api?.verifyToken()
                }

                if (response != null && response.success) {
                    _destination.value = SplashDestination.Home
                } else {
                    clearAuth(context)
                    _destination.value = SplashDestination.SignIn
                }
            } catch (e: Exception) {
                // If offline or connection fails, do not hang endlessly
                clearAuth(context)
                _destination.value = SplashDestination.SignIn
            }
        }
    }
}
