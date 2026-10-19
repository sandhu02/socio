package com.example.cusotmauthtest.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cusotmauthtest.AuthTestScreens
import com.example.cusotmauthtest.AuthTestViewModelFactory
import com.example.cusotmauthtest.retrofit.ChatMessage
import com.example.cusotmauthtest.ui.theme.SocioBackground
import com.example.cusotmauthtest.ui.theme.SocioCardBorder
import com.example.cusotmauthtest.ui.theme.SocioInputBackground
import com.example.cusotmauthtest.ui.theme.SocioSurface
import com.example.cusotmauthtest.ui.theme.SocioSurfaceVariant
import com.example.cusotmauthtest.ui.theme.SocioTextMuted
import com.example.cusotmauthtest.ui.theme.SocioTextPrimary
import com.example.cusotmauthtest.ui.theme.SocioTextSecondary
import com.example.cusotmauthtest.ui.theme.SocioYellow

@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = viewModel(factory = AuthTestViewModelFactory(context = LocalContext.current)),
    sharedVideoViewModel: SharedVideoViewModel
) {
    val recipientUser by sharedVideoViewModel.chatRecipientUser.collectAsState()
    val username = recipientUser?.username ?: "mira.r"
    val initials = recipientUser?.name?.take(2)?.uppercase()
        ?: username.take(2).uppercase().ifBlank { "MR" }

    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    DisposableEffect(Unit) {
        viewModel.connect()
        onDispose {
            viewModel.disconnect()
        }
    }

    LaunchedEffect(uiState.messageList.size) {
        if (uiState.messageList.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messageList.size - 1)
        }
    }

    Scaffold(
        containerColor = SocioBackground,
        topBar = {
            // Header: Back button, Avatar, @handle + online, Call button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SocioBackground)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                Box(
                    modifier = Modifier
                        .size(42.dp)
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

                Spacer(modifier = Modifier.width(12.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SocioSurfaceVariant)
                        .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Handle and status
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "@$username",
                        color = SocioTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "online",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = SocioYellow
                    )
                }

                // Phone Call Action Button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SocioSurface)
                        .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            navController.navigate(AuthTestScreens.AudioCallScreen.name)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Call",
                        tint = SocioYellow,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        bottomBar = {
            // Bottom Message Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SocioBackground)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Paperclip attachment button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SocioInputBackground)
                        .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp))
                        .clickable { /* Attachment flow */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach",
                        tint = SocioTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Message Text Field
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    value = uiState.senderMessage,
                    onValueChange = { viewModel.updateSenderMessage(it) },
                    placeholder = {
                        Text(
                            text = "message @$username",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = SocioTextMuted
                        )
                    },
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

                Spacer(modifier = Modifier.width(10.dp))

                // Send Button (Vibrant Yellow)
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SocioYellow)
                        .clickable {
                            if (uiState.senderMessage.isNotBlank()) {
                                viewModel.sendMessage(
                                    message = uiState.senderMessage,
                                    toUserName = username
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(uiState.messageList) { message ->
                if (message.isCallEvent) {
                    // Centered call event badge: 📞 Audio call · 12:41
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SocioSurface)
                                .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call",
                                tint = SocioYellow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = message.text,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                color = SocioTextPrimary
                            )
                        }
                    }
                } else if (message.isFromMe) {
                    // Sent Message (Right Aligned, Yellow Bubble)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = 14.dp,
                                        bottomEnd = 3.dp
                                    )
                                )
                                .background(SocioYellow)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = Color.Black,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                } else {
                    // Received Message (Left Aligned, Dark Bubble)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = 3.dp,
                                        bottomEnd = 14.dp
                                    )
                                )
                                .background(SocioSurfaceVariant)
                                .border(
                                    BorderStroke(1.dp, SocioCardBorder),
                                    RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = 3.dp,
                                        bottomEnd = 14.dp
                                    )
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = message.text,
                                color = Color.White,
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}