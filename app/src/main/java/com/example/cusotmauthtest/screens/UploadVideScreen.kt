package com.example.cusotmauthtest.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cusotmauthtest.AuthTestViewModelFactory
import com.example.cusotmauthtest.retrofit.Failure
import com.example.cusotmauthtest.retrofit.Loading
import com.example.cusotmauthtest.retrofit.Success
import com.example.cusotmauthtest.ui.theme.SocioBackground
import com.example.cusotmauthtest.ui.theme.SocioCardBorder
import com.example.cusotmauthtest.ui.theme.SocioGoogleBtnBg
import com.example.cusotmauthtest.ui.theme.SocioInputBackground
import com.example.cusotmauthtest.ui.theme.SocioSurface
import com.example.cusotmauthtest.ui.theme.SocioTextMuted
import com.example.cusotmauthtest.ui.theme.SocioTextPrimary
import com.example.cusotmauthtest.ui.theme.SocioTextSecondary
import com.example.cusotmauthtest.ui.theme.SocioYellow

@Composable
fun UploadVideScreen(
    navController: NavController,
    viewModel: UploadVideoViewModel = viewModel(factory = AuthTestViewModelFactory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setVideoUri(uri)
    }

    LaunchedEffect(uiState.response) {
        when (val res = uiState.response) {
            is Success -> {
                Toast.makeText(context, res.message, Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            else -> {}
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header with back button and "New video"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SocioSurface)
                            .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp))
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SocioTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "New video",
                        color = SocioTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Video Preview Area (Dashed yellow border)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SocioInputBackground)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (uiState.selectedVideoUri != null) SocioYellow else SocioYellow.copy(alpha = 0.5f)
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { videoPickerLauncher.launch("video/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, SocioYellow, CircleShape)
                                .background(SocioYellow.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = SocioYellow,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (uiState.selectedVideoUri != null) {
                                uiState.selectedVideoName ?: "video selected"
                            } else {
                                "video preview"
                            },
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = if (uiState.selectedVideoUri != null) SocioYellow else SocioTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // "Choose video from device" Button
                OutlinedButton(
                    onClick = { videoPickerLauncher.launch("video/*") },
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
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Upload",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Choose video from device",
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title Field
                Text(
                    text = "title",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = {
                        Text(
                            text = "What is this video about?",
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

                Spacer(modifier = Modifier.height(18.dp))

                // Description Field
                Text(
                    text = "description",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    placeholder = {
                        Text(
                            text = "Add details, links, timestamps",
                            fontFamily = FontFamily.Monospace,
                            color = SocioTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SocioInputBackground,
                        unfocusedContainerColor = SocioInputBackground,
                        focusedBorderColor = SocioYellow,
                        unfocusedBorderColor = SocioCardBorder,
                        focusedTextColor = SocioTextPrimary,
                        unfocusedTextColor = SocioTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Who can watch segmented control
                Text(
                    text = "who can watch",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SocioInputBackground)
                        .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val options = listOf("public", "followers", "private")
                    options.forEach { option ->
                        val isSelected = uiState.whoCanWatch == option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SocioYellow else Color.Transparent)
                                .clickable { viewModel.updateWhoCanWatch(option) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else SocioTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Publish Button
                Button(
                    onClick = { viewModel.uploadVideo() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SocioYellow,
                        contentColor = Color.Black
                    ),
                    enabled = uiState.response !is Loading
                ) {
                    if (uiState.response is Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.Black,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Publish",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                when (val res = uiState.response) {
                    is Failure -> {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = res.message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

// Alias for convenience
@Composable
fun UploadVideoScreen(
    navController: NavController,
    viewModel: UploadVideoViewModel = viewModel(factory = AuthTestViewModelFactory(LocalContext.current))
) = UploadVideScreen(navController = navController, viewModel = viewModel)
