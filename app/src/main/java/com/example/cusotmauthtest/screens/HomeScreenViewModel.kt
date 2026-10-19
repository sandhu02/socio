package com.example.cusotmauthtest.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusotmauthtest.retrofit.Video
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val videosResponse: VideosResponse? = null ,
    val logoutResponse: LogoutResponse? = null,
    val deleteResponse: DeleteResponse? = null,
    val showingDialog: Boolean = false
)

class HomeScreenViewModel(val context: Context) : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    fun getVideos(){
        viewModelScope.launch {
            _homeUiState.update { it.copy(videosResponse = VideosResponse.Loading) }
            try {
                val response = kotlinx.coroutines.withTimeoutOrNull(5000L) {
                    getAuthApiInstance(context = context)?.getVideos()
                }

                if (response == null){
                    _homeUiState.update { it.copy(videosResponse = VideosResponse.Failure("Server unavailable or offline")) }
                }
                else {
                    _homeUiState.update { it.copy(videosResponse = VideosResponse.Success(response) ) }
                }
            }
            catch(e: Exception){
                _homeUiState.update { it.copy(videosResponse = VideosResponse.Failure("Offline: ${e.localizedMessage}") ) }
            }
        }
    }

    fun deleteVideo(video: Video) {
        viewModelScope.launch {
            _homeUiState.update { it.copy(deleteResponse = DeleteResponse.Loading, showingDialog = true) }
            try {
                val response = getAuthApiInstance(context = context)?.deleteVideo(video.publicId)

                if (response == null){
                    _homeUiState.update { it.copy(deleteResponse = DeleteResponse.Failure("Empty Response"), showingDialog = true) }
                }
                else if (!response.success){
                    _homeUiState.update { it.copy(deleteResponse = DeleteResponse.Failure(response.message), showingDialog = true) }
                }
                else {
                    _homeUiState.update { it.copy(deleteResponse = DeleteResponse.Success(response.message), showingDialog = true ) }
                    getVideos()
                }
            }
            catch(e: Exception){
                _homeUiState.update { it.copy(deleteResponse = DeleteResponse.Failure("Network Error: ${e.localizedMessage}"), showingDialog = true ) }
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            _homeUiState.update { it.copy(logoutResponse = LogoutResponse.Loading) }
            try {
                val response = getAuthApiInstance(context = context)?.logout()

                if (response == null){
                    _homeUiState.update { it.copy(logoutResponse = LogoutResponse.Failure("Empty Response")) }
                }
                else if (response.success == false){
                    _homeUiState.update { it.copy(logoutResponse = LogoutResponse.Failure(response.message) ) }
                }
                else {
                    com.example.cusotmauthtest.retrofit.clearAuth(context)
                    _homeUiState.update { it.copy(logoutResponse = LogoutResponse.Success(response.message) ) }
                }
            }
            catch(e: Exception){
                com.example.cusotmauthtest.retrofit.clearAuth(context)
                _homeUiState.update { it.copy(logoutResponse = LogoutResponse.Success("Logged out locally") ) }
            }
        }
    }

    fun closeDialog(){
        _homeUiState.update { it.copy(deleteResponse = null, showingDialog = false) }
    }

    init {
        getVideos()
    }
}

sealed class LogoutResponse() {
    data class Success(val message: String) : LogoutResponse()
    data class Failure(val message: String) : LogoutResponse()
    object Loading : LogoutResponse()
}

sealed class VideosResponse() {
    data class Success(val videoList : List<Video>) : VideosResponse()
    data class Failure(val error : String) : VideosResponse()
    object Loading : VideosResponse()
}

sealed class DeleteResponse() {
    data class Success(val message: String) : DeleteResponse()
    data class Failure(val message: String) : DeleteResponse()
    object Loading : DeleteResponse()
}