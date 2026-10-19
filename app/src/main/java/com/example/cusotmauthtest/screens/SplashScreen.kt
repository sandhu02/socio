package com.example.cusotmauthtest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cusotmauthtest.AuthTestScreens
import com.example.cusotmauthtest.AuthTestViewModelFactory

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel(factory = AuthTestViewModelFactory(LocalContext.current))
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            is SplashDestination.Home -> {
                navController.navigate(AuthTestScreens.HomeScreen.name) {
                    popUpTo(AuthTestScreens.SplashScreen.name) { inclusive = true }
                }
            }
            is SplashDestination.SignIn -> {
                navController.navigate(AuthTestScreens.SignInScreen.name) {
                    popUpTo(AuthTestScreens.SplashScreen.name) { inclusive = true }
                }
            }
            is SplashDestination.Loading -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(com.example.cusotmauthtest.ui.theme.SocioBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "socio",
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = Color.White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                        letterSpacing = (-1).sp
                    ),
                    fontSize = 38.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(com.example.cusotmauthtest.ui.theme.SocioYellow)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "video · chat · voice",
                style = MaterialTheme.typography.labelMedium,
                color = com.example.cusotmauthtest.ui.theme.SocioTextSecondary
            )
            Spacer(modifier = Modifier.height(36.dp))
            CircularProgressIndicator(
                color = com.example.cusotmauthtest.ui.theme.SocioYellow,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
