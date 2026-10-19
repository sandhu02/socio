package com.example.cusotmauthtest.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cusotmauthtest.AuthTestScreens
import com.example.cusotmauthtest.AuthTestViewModelFactory
import com.example.cusotmauthtest.retrofit.Failure
import com.example.cusotmauthtest.retrofit.Loading
import com.example.cusotmauthtest.retrofit.Success
import com.example.cusotmauthtest.ui.theme.SocioBackground
import com.example.cusotmauthtest.ui.theme.SocioCardBorder
import com.example.cusotmauthtest.ui.theme.SocioGoogleBtnBg
import com.example.cusotmauthtest.ui.theme.SocioInputBackground
import com.example.cusotmauthtest.ui.theme.SocioTextMuted
import com.example.cusotmauthtest.ui.theme.SocioTextPrimary
import com.example.cusotmauthtest.ui.theme.SocioTextSecondary
import com.example.cusotmauthtest.ui.theme.SocioYellow

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel(factory = AuthTestViewModelFactory(LocalContext.current))
) {
    val registerUiState by viewModel.registerUiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(registerUiState.response) {
        if (registerUiState.response is Success) {
            navController.navigate(AuthTestScreens.SignInScreen.name) {
                popUpTo(AuthTestScreens.RegisterScreen.name) { inclusive = true }
            }
        }
    }

    Scaffold(
        containerColor = SocioBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Brand Header: "socio ■"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                ) {
                    Text(
                        text = "socio",
                        color = SocioTextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .background(SocioYellow)
                    )
                }

                // Main Headline
                Text(
                    text = "Pick an id.\nIt's yours alone.",
                    color = SocioTextPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Subtitle
                Text(
                    text = "Your username is your unique id. People find, text and call you by it.",
                    color = SocioTextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Email Field
                Text(
                    text = "email",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = registerUiState.email,
                    onValueChange = { viewModel.updateEmail(it) },
                    placeholder = {
                        Text(
                            text = "you@domain.dev",
                            fontFamily = FontFamily.Monospace,
                            color = SocioTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SocioInputBackground,
                        unfocusedContainerColor = SocioInputBackground,
                        focusedBorderColor = SocioYellow,
                        unfocusedBorderColor = SocioCardBorder,
                        focusedTextColor = SocioTextPrimary,
                        unfocusedTextColor = SocioTextPrimary
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Username Field
                Text(
                    text = "username (unique id)",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = registerUiState.username,
                    onValueChange = { viewModel.updateUsername(it) },
                    placeholder = {
                        Text(
                            text = "@mira.r",
                            fontFamily = FontFamily.Monospace,
                            color = SocioTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SocioInputBackground,
                        unfocusedContainerColor = SocioInputBackground,
                        focusedBorderColor = SocioYellow,
                        unfocusedBorderColor = SocioCardBorder,
                        focusedTextColor = SocioTextPrimary,
                        unfocusedTextColor = SocioTextPrimary
                    ),
                    singleLine = true
                )

                // Available feedback
                val displayTag = if (registerUiState.username.isNotBlank()) {
                    "@${registerUiState.username}"
                } else {
                    "@mira.r"
                }
                Text(
                    text = "$displayTag is available",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = SocioYellow,
                    modifier = Modifier.padding(top = 6.dp, start = 2.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Password Field
                Text(
                    text = "password",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = registerUiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    placeholder = {
                        Text(
                            text = "••••••••",
                            fontFamily = FontFamily.Monospace,
                            color = SocioTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SocioInputBackground,
                        unfocusedContainerColor = SocioInputBackground,
                        focusedBorderColor = SocioYellow,
                        unfocusedBorderColor = SocioCardBorder,
                        focusedTextColor = SocioTextPrimary,
                        unfocusedTextColor = SocioTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Create Account Button
                Button(
                    onClick = { viewModel.register() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SocioYellow,
                        contentColor = Color.Black
                    ),
                    enabled = registerUiState.response !is Loading
                ) {
                    if (registerUiState.response is Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.Black,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Create account",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Divider: "or"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = SocioCardBorder,
                        thickness = 1.dp
                    )
                    Text(
                        text = "or",
                        color = SocioTextMuted,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = SocioCardBorder,
                        thickness = 1.dp
                    )
                }

                // Continue with Google Button
                OutlinedButton(
                    onClick = { /* Google Sign In UI flow */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = SocioGoogleBtnBg,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, SocioCardBorder)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G",
                            color = SocioYellow,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Error / Success message feedback
                when (val res = registerUiState.response) {
                    is Failure -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = res.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is Success -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = res.message,
                            color = SocioYellow,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {}
                }
            }

            // Footer: Have an account? Log in
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Have an account? ",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary
                )
                Text(
                    text = "Log in",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SocioYellow,
                    modifier = Modifier.clickable {
                        navController.navigate(AuthTestScreens.SignInScreen.name) {
                            popUpTo(AuthTestScreens.RegisterScreen.name) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}