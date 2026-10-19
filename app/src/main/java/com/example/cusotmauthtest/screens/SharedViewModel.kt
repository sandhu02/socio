package com.example.cusotmauthtest.screens

import androidx.lifecycle.ViewModel
import com.example.cusotmauthtest.retrofit.User
import com.example.cusotmauthtest.retrofit.Video
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SharedVideoViewModel : ViewModel() {
    private val _selectedVideo = MutableStateFlow<Video?>(null)
    val selectedVideo = _selectedVideo.asStateFlow()

    private val _chatRecipientUser = MutableStateFlow<User?>(null)
    val chatRecipientUser = _chatRecipientUser.asStateFlow()

    fun setVideo(video: Video) {
        _selectedVideo.value = video
    }

    fun setChatUser(user : User) {
        _chatRecipientUser.value = user
    }
}
