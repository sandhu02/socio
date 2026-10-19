package com.example.cusotmauthtest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cusotmauthtest.screens.ChatViewModel
import com.example.cusotmauthtest.screens.HomeScreenViewModel
import com.example.cusotmauthtest.screens.RegisterViewModel
import com.example.cusotmauthtest.screens.SignInViewModel
import com.example.cusotmauthtest.screens.SplashViewModel
import com.example.cusotmauthtest.screens.UploadVideoViewModel
import com.example.cusotmauthtest.screens.UsersForChatViewModel

class AuthTestViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(context) as T
        }
        else if (modelClass.isAssignableFrom(SignInViewModel::class.java)){
            return SignInViewModel(context) as T
        }
        else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)){
            return RegisterViewModel(context) as T
        }
        else if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)){
            return HomeScreenViewModel(context) as T
        }
        else if (modelClass.isAssignableFrom(UsersForChatViewModel::class.java)) {
            return UsersForChatViewModel(context) as T
        }
        else if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(context) as T
        }
        else if (modelClass.isAssignableFrom(UploadVideoViewModel::class.java)) {
            return UploadVideoViewModel(context) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}