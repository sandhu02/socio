package com.example.cusotmauthtest.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.cusotmauthtest.retrofit.User
import com.example.cusotmauthtest.ui.theme.SocioBackground
import com.example.cusotmauthtest.ui.theme.SocioCardBorder
import com.example.cusotmauthtest.ui.theme.SocioSurface
import com.example.cusotmauthtest.ui.theme.SocioSurfaceVariant
import com.example.cusotmauthtest.ui.theme.SocioTextPrimary
import com.example.cusotmauthtest.ui.theme.SocioTextSecondary
import com.example.cusotmauthtest.ui.theme.SocioYellow

@Composable
fun UsersForChatScreen(
    navController: NavController,
    viewModel: UsersForChatViewModel = viewModel(factory = AuthTestViewModelFactory(LocalContext.current)),
    sharedViewModel: SharedVideoViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Default sample users matching the design
    val sampleUsers = remember {
        listOf(
            User(name = "Mira Ray", username = "mira.r"),
            User(name = "Null Pointer", username = "nullpointer"),
            User(name = "Dev Lead", username = "devlead")
        )
    }

    Scaffold(
        containerColor = SocioBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "chats",
                    color = SocioTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val usersToShow: List<User> = when (val state = uiState) {
                is UsersUiState.Success -> {
                    if (state.users.isNotEmpty()) state.users else sampleUsers
                }
                else -> sampleUsers
            }

            if (uiState is UsersUiState.Loading) {
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(usersToShow) { user ->
                    SocioUserCard(
                        user = user,
                        onClick = {
                            sharedViewModel.setChatUser(user)
                            navController.navigate(AuthTestScreens.ChatScreen.name)
                        },
                        onCallClick = {
                            sharedViewModel.setChatUser(user)
                            navController.navigate(AuthTestScreens.AudioCallScreen.name)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SocioUserCard(
    user: User,
    onClick: () -> Unit,
    onCallClick: () -> Unit
) {
    val initials = user.name.take(2).uppercase().ifBlank {
        user.username.take(2).uppercase()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SocioSurface)
            .border(1.dp, SocioCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Box
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SocioSurfaceVariant)
                .border(1.dp, SocioCardBorder, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                color = SocioTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "@${user.username}",
                fontFamily = FontFamily.Monospace,
                color = SocioYellow,
                fontSize = 13.sp
            )
        }

        // Quick Call Button
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SocioSurfaceVariant)
                .border(1.dp, SocioCardBorder, RoundedCornerShape(10.dp))
                .clickable { onCallClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Call",
                tint = SocioYellow,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
