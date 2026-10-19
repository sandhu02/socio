package com.example.cusotmauthtest.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cusotmauthtest.ui.theme.SocioBackground
import com.example.cusotmauthtest.ui.theme.SocioCallRed
import com.example.cusotmauthtest.ui.theme.SocioSurfaceVariant
import com.example.cusotmauthtest.ui.theme.SocioTextPrimary
import com.example.cusotmauthtest.ui.theme.SocioTextSecondary
import com.example.cusotmauthtest.ui.theme.SocioYellow
import kotlinx.coroutines.delay

@Composable
fun AudioCallScreen(
    navController: NavController,
    sharedVideoViewModel: SharedVideoViewModel
) {
    val recipientUser by sharedVideoViewModel.chatRecipientUser.collectAsState()
    val username = recipientUser?.username ?: "mira.r"
    val initials = recipientUser?.name?.take(2)?.uppercase()
        ?: username.take(2).uppercase().ifBlank { "MR" }

    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }

    // Live timer starting from 12:41 (761 seconds) as displayed in screenshot
    var callSeconds by remember { mutableIntStateOf(761) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            callSeconds++
        }
    }

    val minutes = callSeconds / 60
    val seconds = callSeconds % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    // Animated voice wave bars
    val infiniteTransition = rememberInfiniteTransition(label = "WaveTransition")
    val animFactor1 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim1"
    )
    val animFactor2 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim2"
    )
    val animFactor3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(480, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anim3"
    )

    Scaffold(
        containerColor = SocioBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top encryption status: "socio audio · encrypted"
            Text(
                text = "socio audio · encrypted",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = SocioTextSecondary,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Center Call Profile: Large Avatar, @username, Timer & Waveform
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Large Avatar with yellow outline
                Box(
                    modifier = Modifier
                        .size(136.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SocioSurfaceVariant)
                        .border(BorderStroke(1.5.dp, SocioYellow), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Handle: @mira.r
                Text(
                    text = "@$username",
                    color = SocioTextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Timer: 12:41
                Text(
                    text = timerText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = SocioYellow
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Audio waveform visualizer (yellow vertical bars)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(36.dp)
                ) {
                    val baseHeights = listOf(14, 22, 34, 18, 36, 28, 20, 32, 16, 24)
                    val factors = listOf(
                        animFactor1, animFactor2, animFactor3, animFactor1,
                        animFactor2, animFactor3, animFactor1, animFactor2,
                        animFactor3, animFactor1
                    )
                    baseHeights.forEachIndexed { i, h ->
                        val dynamicHeight = (h * factors[i % factors.size]).coerceIn(6f, 36f)
                        Box(
                            modifier = Modifier
                                .size(width = 3.5.dp, height = dynamicHeight.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(SocioYellow)
                        )
                    }
                }
            }

            // Bottom Control Dock: 4 rounded buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Button
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isMuted) Color(0xFF2C2C2E) else SocioSurfaceVariant)
                        .clickable { isMuted = !isMuted },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mute",
                        tint = if (isMuted) SocioYellow else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Speaker Button
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSpeakerOn) SocioSurfaceVariant else Color(0xFF2C2C2E))
                        .clickable { isSpeakerOn = !isSpeakerOn },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Speaker",
                        tint = if (isSpeakerOn) SocioYellow else SocioTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Video / Camera Button
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SocioSurfaceVariant)
                        .clickable { /* Toggle video */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // End Call Button (Red)
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(SocioCallRed)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
