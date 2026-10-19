package com.example.cusotmauthtest.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cusotmauthtest.AuthTestScreens
import com.example.cusotmauthtest.AuthTestViewModelFactory
import com.example.cusotmauthtest.retrofit.Video
import com.example.cusotmauthtest.ui.theme.SocioBackground
import com.example.cusotmauthtest.ui.theme.SocioCardBorder
import com.example.cusotmauthtest.ui.theme.SocioInputBackground
import com.example.cusotmauthtest.ui.theme.SocioSurface
import com.example.cusotmauthtest.ui.theme.SocioSurfaceVariant
import com.example.cusotmauthtest.ui.theme.SocioTextMuted
import com.example.cusotmauthtest.ui.theme.SocioTextPrimary
import com.example.cusotmauthtest.ui.theme.SocioTextSecondary
import com.example.cusotmauthtest.ui.theme.SocioYellow

data class SocioFeedItem(
    val id: String,
    val authorInitials: String,
    val authorHandle: String,
    val title: String,
    val views: String,
    val timestamp: String,
    val videoUrl: String? = null,
    val rawVideo: Video? = null
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = viewModel(factory = AuthTestViewModelFactory(LocalContext.current)),
    sharedViewModel: SharedVideoViewModel
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("for_you") }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Fallback sample feed items matching Page 3 screenshot perfectly
    val sampleVideos = remember {
        listOf(
            SocioFeedItem(
                id = "sample_1",
                authorInitials = "NL",
                authorHandle = "@nullpointer",
                title = "Rewriting 14 commits live, and nothing broke",
                views = "48K views",
                timestamp = "3d"
            ),
            SocioFeedItem(
                id = "sample_2",
                authorInitials = "MR",
                authorHandle = "@mira.r",
                title = "Designing clean audio interfaces with Compose",
                views = "21K views",
                timestamp = "1d"
            )
        )
    }

    Scaffold(
        containerColor = SocioBackground,
        bottomBar = {
            SocioBottomNavigationBar(
                currentScreen = "home",
                onTabSelected = { tab ->
                    when (tab) {
                        "home" -> { /* Already on home */ }
                        "upload" -> navController.navigate(AuthTestScreens.UploadVideoScreen.name)
                        "chats" -> navController.navigate(AuthTestScreens.ChatUsersScreen.name)
                        "calls" -> navController.navigate(AuthTestScreens.AudioCallScreen.name)
                        "me" -> showProfileDialog = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header: "socio ■" logo on left, search icon on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
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

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SocioSurface)
                        .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            Toast.makeText(context, "Search coming soon", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SocioTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Segmented Category Tabs: for_you | following | live
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SocioSurface)
                    .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf("for_you", "following", "live")
                tabs.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SocioYellow else Color.Transparent)
                            .clickable { selectedTab = tab },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else SocioTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video Feed List
            val feedItems: List<SocioFeedItem> = when (val res = homeUiState.videosResponse) {
                is VideosResponse.Success -> {
                    if (res.videoList.isNotEmpty()) {
                        res.videoList.map { vid ->
                            val userPart = vid.publicId.take(6).ifBlank { "dev" }
                            val initials = vid.title.take(2).uppercase().ifBlank { "SC" }
                            SocioFeedItem(
                                id = vid.publicId,
                                authorInitials = initials,
                                authorHandle = "@$userPart",
                                title = vid.title,
                                views = "1.2K views",
                                timestamp = "Just now",
                                videoUrl = vid.url,
                                rawVideo = vid
                            )
                        }
                    } else {
                        sampleVideos
                    }
                }
                else -> sampleVideos
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                // If loading, show sleek top indicator
                if (homeUiState.videosResponse is VideosResponse.Loading) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SocioYellow,
                                strokeWidth = 2.dp
                            )
                        }
                    }
                }

                items(feedItems, key = { it.id }) { item ->
                    SocioVideoCard(
                        feedItem = item,
                        onClick = {
                            if (item.rawVideo != null) {
                                sharedViewModel.setVideo(item.rawVideo)
                                navController.navigate(AuthTestScreens.ViewVideoScreen.name)
                            } else {
                                // For sample item, open with placeholder or mock Video object
                                val mockVideo = Video(
                                    publicId = item.id,
                                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                                    title = item.title,
                                    description = "Demonstration video for ${item.authorHandle}"
                                )
                                sharedViewModel.setVideo(mockVideo)
                                navController.navigate(AuthTestScreens.ViewVideoScreen.name)
                            }
                        },
                        onDelete = {
                            if (item.rawVideo != null) {
                                viewModel.deleteVideo(item.rawVideo)
                            }
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Logout Feedback
            when (val response = homeUiState.logoutResponse) {
                is LogoutResponse.Failure -> {
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                }
                is LogoutResponse.Success -> {
                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                    navController.navigate(AuthTestScreens.SignInScreen.name) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is LogoutResponse.Loading -> {}
                null -> {}
            }

            // Delete Dialog Feedback
            when (val response = homeUiState.deleteResponse) {
                is DeleteResponse.Failure -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.closeDialog() },
                        title = { Text("Notice", color = SocioTextPrimary) },
                        text = { Text(response.message, color = SocioTextSecondary) },
                        containerColor = SocioSurface,
                        confirmButton = {
                            Button(
                                onClick = { viewModel.closeDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = SocioYellow, contentColor = Color.Black)
                            ) {
                                Text("Ok")
                            }
                        }
                    )
                }
                is DeleteResponse.Success -> {
                    AlertDialog(
                        onDismissRequest = { viewModel.closeDialog() },
                        title = { Text("Success", color = SocioTextPrimary) },
                        text = { Text(response.message, color = SocioTextSecondary) },
                        containerColor = SocioSurface,
                        confirmButton = {
                            Button(
                                onClick = { viewModel.closeDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = SocioYellow, contentColor = Color.Black)
                            ) {
                                Text("Ok")
                            }
                        }
                    )
                }
                else -> {}
            }

            // Profile / Me Dialog
            if (showProfileDialog) {
                AlertDialog(
                    onDismissRequest = { showProfileDialog = false },
                    containerColor = SocioSurface,
                    title = {
                        Text("Profile & Settings", color = SocioTextPrimary, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column {
                            Text(
                                "Logged in as @${com.example.cusotmauthtest.retrofit.getUsername(context) ?: "user"}",
                                color = SocioYellow,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Base URL: ${com.example.cusotmauthtest.retrofit.getBaseUrl(context)}", color = SocioTextSecondary, fontSize = 12.sp)
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showProfileDialog = false
                                viewModel.logout()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.example.cusotmauthtest.ui.theme.SocioCallRed,
                                contentColor = Color.White
                            )
                        ) {
                            Text("Logout")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showProfileDialog = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SocioSurfaceVariant,
                                contentColor = SocioTextPrimary
                            )
                        ) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SocioVideoCard(
    feedItem: SocioFeedItem,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Video box with dashed yellow/subtle border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SocioInputBackground)
                .border(
                    BorderStroke(1.dp, SocioYellow.copy(alpha = 0.5f)),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
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

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "video goes here",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = SocioTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Metadata row: Avatar box + Title & Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // User Avatar box (e.g. "NL" in rounded box)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SocioSurfaceVariant)
                    .border(1.dp, SocioCardBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = feedItem.authorInitials,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = feedItem.title,
                    color = SocioTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = feedItem.authorHandle,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = SocioYellow
                    )
                    Text(
                        text = " · ${feedItem.views} · ${feedItem.timestamp}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = SocioTextSecondary
                    )
                }
            }

            if (feedItem.rawVideo != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = SocioTextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SocioBottomNavigationBar(
    currentScreen: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0A))
            .border(BorderStroke(1.dp, Color(0xFF161616)))
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "home",
            icon = Icons.Default.Home,
            isSelected = currentScreen == "home",
            onClick = { onTabSelected("home") }
        )
        BottomNavItem(
            label = "upload",
            icon = Icons.Default.Add,
            isSelected = currentScreen == "upload",
            onClick = { onTabSelected("upload") }
        )
        BottomNavItem(
            label = "chats",
            icon = Icons.Default.ChatBubbleOutline,
            isSelected = currentScreen == "chats",
            onClick = { onTabSelected("chats") }
        )
        BottomNavItem(
            label = "calls",
            icon = Icons.Default.Call,
            isSelected = currentScreen == "calls",
            onClick = { onTabSelected("calls") }
        )
        BottomNavItem(
            label = "me",
            icon = Icons.Default.PersonOutline,
            isSelected = currentScreen == "me",
            onClick = { onTabSelected("me") }
        )
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) SocioYellow else SocioTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (isSelected) SocioYellow else SocioTextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
