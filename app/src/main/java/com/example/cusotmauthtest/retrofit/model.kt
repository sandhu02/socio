package com.example.cusotmauthtest.retrofit

sealed class Response
object Loading : Response()
data class Success(val message: String) : Response()
data class Failure(val message: String) : Response()

data class VerifyTokenResponse(
    val success: Boolean,
    val message: String
)

data class RegisterRequest(
    val name: String,
    val username: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val message: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val success: Boolean,
    val token: String
)

data class LogoutResponse(
    val success: Boolean,
    val message: String
)

data class Video(
    val url: String,
    val publicId: String,
    val title: String,
    val description: String
)

data class VideoResponse(
    val success: Boolean,
    val message: String
)

data class DeleteVideoResponse(
    val success: Boolean,
    val message: String
)

data class User(
    val _id: String? = null,
    val name: String,
    val username: String,
    val password: String? = null,
    val fcmTokens: List<String>? = emptyList()
)

data class RegisterNotificationRequest(
    val username: String,
    val fcmtoken: String
)

data class SendNotificationRequest(
    val title: String,
    val body: String
)

data class GenericResponse(
    val success: Boolean,
    val message: String
)

data class ChatMessage(
    val text: String,
    val isFromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val senderUsername: String? = null,
    val isCallEvent: Boolean = false
)