package com.example.cusotmauthtest.retrofit

import android.content.Context
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AuthApiService {
    @GET("/")
    suspend fun verifyToken(): VerifyTokenResponse

    @POST("/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/register")
    suspend fun register(@Body registerRequest: RegisterRequest): RegisterResponse

    @GET("/videos")
    suspend fun getVideos(): List<Video>

    @Multipart
    @POST("/videos")
    suspend fun uploadVideo(
        @Part video: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody
    ): VideoResponse

    @DELETE("/videos")
    suspend fun deleteVideo(@Query("publicId") publicId: String): DeleteVideoResponse

    @GET("/users")
    suspend fun getUsers(): List<User>

    @DELETE("/logout")
    suspend fun logout(): LogoutResponse

    @POST("/registerNotification")
    suspend fun registerNotification(@Body request: RegisterNotificationRequest): GenericResponse

    @POST("/notification")
    suspend fun sendNotification(
        @Query("username") username: String,
        @Body request: SendNotificationRequest
    ): GenericResponse
}

const val DEFAULT_BASE_URL: String = "https://x6rjnjfw-3000.inc1.devtunnels.ms/"
var BASE_URL: String = DEFAULT_BASE_URL

fun getBaseUrl(context: Context): String {
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    return prefs.getString("custom_base_url", null) ?: BASE_URL
}

fun setBaseUrl(context: Context, url: String) {
    val formatted = if (url.endsWith("/")) url else "$url/"
    val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("custom_base_url", formatted).apply()
    BASE_URL = formatted
}

fun getAuthApiInstance(
    context: Context
): AuthApiService? {
    val token = getToken(context) ?: ""
    val baseUrl = getBaseUrl(context)

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            if (token.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    return retrofit.create(AuthApiService::class.java)
}
