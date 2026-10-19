package com.example.cusotmauthtest.screens

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cusotmauthtest.retrofit.Response
import com.example.cusotmauthtest.retrofit.Loading
import com.example.cusotmauthtest.retrofit.Success
import com.example.cusotmauthtest.retrofit.Failure
import com.example.cusotmauthtest.retrofit.getAuthApiInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

data class UploadVideoUiState(
    val title: String = "",
    val description: String = "",
    val whoCanWatch: String = "public",
    val selectedVideoUri: Uri? = null,
    val selectedVideoName: String? = null,
    val response: Response? = null
)

class UploadVideoViewModel(val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(UploadVideoUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun updateWhoCanWatch(who: String) {
        _uiState.update { it.copy(whoCanWatch = who) }
    }

    fun setVideoUri(uri: Uri?, name: String? = null) {
        _uiState.update { it.copy(selectedVideoUri = uri, selectedVideoName = name ?: uri?.lastPathSegment) }
    }

    fun uploadVideo() {
        val state = _uiState.value
        val uri = state.selectedVideoUri

        if (uri == null) {
            _uiState.update { it.copy(response = Failure("Please select a video")) }
            return
        }
        if (state.title.isBlank()) {
            _uiState.update { it.copy(response = Failure("Please enter a title")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(response = Loading) }
            try {
                val api = getAuthApiInstance(context)
                if (api == null) {
                    _uiState.update { it.copy(response = Failure("Authentication error. Please log in again.")) }
                    return@launch
                }

                // Copy stream to temp cache file for multipart upload
                val tempFile = withContext(Dispatchers.IO) {
                    val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.mp4")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }
                    file
                }

                val requestFile = tempFile.asRequestBody("video/*".toMediaTypeOrNull())
                val videoPart = MultipartBody.Part.createFormData("video", tempFile.name, requestFile)
                val titleBody = state.title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = state.description.toRequestBody("text/plain".toMediaTypeOrNull())

                val res = api.uploadVideo(videoPart, titleBody, descBody)

                // Clean up temp file
                tempFile.delete()

                if (res.success) {
                    _uiState.update { it.copy(response = Success(res.message)) }
                } else {
                    _uiState.update { it.copy(response = Failure(res.message)) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(response = Failure("Upload failed: ${e.localizedMessage}")) }
            }
        }
    }

    fun resetResponse() {
        _uiState.update { it.copy(response = null) }
    }
}
