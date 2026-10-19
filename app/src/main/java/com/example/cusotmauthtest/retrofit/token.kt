package com.example.cusotmauthtest.retrofit

import android.content.Context

private const val PREFS_NAME = "auth_prefs"
private const val KEY_JWT_TOKEN = "jwt_token"
private const val KEY_USERNAME = "current_username"
private const val KEY_FCM_TOKEN = "fcm_token"

fun saveToken(context: Context, token: String) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString(KEY_JWT_TOKEN, token)
        apply()
    }
}

fun getToken(context: Context): String? {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPref.getString(KEY_JWT_TOKEN, null)
}

fun clearToken(context: Context) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        remove(KEY_JWT_TOKEN)
        apply()
    }
}

fun saveUsername(context: Context, username: String) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString(KEY_USERNAME, username)
        apply()
    }
}

fun getUsername(context: Context): String? {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPref.getString(KEY_USERNAME, null)
}

fun saveFcmToken(context: Context, fcmToken: String) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString(KEY_FCM_TOKEN, fcmToken)
        apply()
    }
}

fun getFcmToken(context: Context): String? {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return sharedPref.getString(KEY_FCM_TOKEN, null)
}

fun clearAuth(context: Context) {
    val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        remove(KEY_JWT_TOKEN)
        remove(KEY_USERNAME)
        apply()
    }
}
