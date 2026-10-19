package com.example.cusotmauthtest

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cusotmauthtest.screens.ChatScreen
import com.example.cusotmauthtest.screens.HomeScreen
import com.example.cusotmauthtest.screens.RegisterScreen
import com.example.cusotmauthtest.screens.SharedVideoViewModel
import com.example.cusotmauthtest.screens.SignInScreen
import com.example.cusotmauthtest.screens.SplashScreen
import com.example.cusotmauthtest.screens.UploadVideScreen
import com.example.cusotmauthtest.screens.UsersForChatScreen
import com.example.cusotmauthtest.screens.ViewVideoScreen

import com.example.cusotmauthtest.screens.AudioCallScreen

enum class AuthTestScreens(val title: String){
    SplashScreen(title = "Splash"),
    SignInScreen(title = "SignIn"),
    RegisterScreen(title = "Register"),
    HomeScreen(title = "Home"),
    UploadVideoScreen(title = "UploadVide"),
    ViewVideoScreen(title = "ViewVideo"),
    ChatUsersScreen(title = "ChatUsers"),
    ChatScreen(title = "ChatScreen"),
    AudioCallScreen(title = "AudioCall")
}

@Composable
fun AuthTestApp(
    navController : NavHostController = rememberNavController(),
    sharedViewModel: SharedVideoViewModel = viewModel()
) {
    NavHost(
        navController = navController ,
        startDestination = AuthTestScreens.SplashScreen.name
    ){
        composable(route = AuthTestScreens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(route = AuthTestScreens.RegisterScreen.name) {
            RegisterScreen(navController = navController)
        }
        composable(route = AuthTestScreens.SignInScreen.name) {
            SignInScreen(navController = navController)
        }
        composable(route = AuthTestScreens.HomeScreen.name) {
            HomeScreen(navController = navController , sharedViewModel = sharedViewModel)
        }
        composable(route = AuthTestScreens.ViewVideoScreen.name) {
            ViewVideoScreen(navController = navController, sharedVideoViewModel = sharedViewModel)
        }
        composable(route = AuthTestScreens.ChatUsersScreen.name) {
            UsersForChatScreen(navController = navController, sharedViewModel = sharedViewModel)
        }
        composable(route = AuthTestScreens.ChatScreen.name) {
            ChatScreen(navController = navController , sharedVideoViewModel = sharedViewModel)
        }
        composable(route = AuthTestScreens.UploadVideoScreen.name) {
            UploadVideScreen(navController = navController)
        }
        composable(route = AuthTestScreens.AudioCallScreen.name) {
            AudioCallScreen(navController = navController, sharedVideoViewModel = sharedViewModel)
        }
    }
}