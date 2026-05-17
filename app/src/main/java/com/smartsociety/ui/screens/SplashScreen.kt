package com.smartsociety.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    // Pulse animation state
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale1"
    )
    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha1"
    )
    
    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing, delayMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale2"
    )
    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing, delayMillis = 500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha2"
    )

    // Fade in up animation states
    val fadeInUpAlpha = remember { Animatable(0f) }
    val fadeInUpOffsetY = remember { Animatable(20f) }

    // Progress bar animation
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run fade-in and progress concurrently
        launch {
            fadeInUpAlpha.animateTo(1f, animationSpec = tween(800, easing = LinearOutSlowInEasing))
        }
        launch {
            fadeInUpOffsetY.animateTo(0f, animationSpec = tween(800, easing = LinearOutSlowInEasing))
        }
        launch {
            delay(300) // Slight delay for progress bar
            progress.animateTo(0.8f, animationSpec = tween(2000, easing = LinearOutSlowInEasing))
        }
        
        // Wait for animations to finish before timeout
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ambient background glows
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-100).dp)
                .size(300.dp)
                .blur(100.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(300.dp)
                .blur(100.dp)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), CircleShape)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Section
            Box(contentAlignment = Alignment.Center) {
                // Pulse Rings
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .scale(pulseScale1)
                        .alpha(pulseAlpha1)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(192.dp)
                        .scale(pulseScale2)
                        .alpha(pulseAlpha2)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                )

                // Core Logo
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh, RoundedCornerShape(32.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(32.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "App Logo",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Typography Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = fadeInUpOffsetY.value.dp)
                    .alpha(fadeInUpAlpha.value)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Socio")
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append(".")
                        }
                    },
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Your premium social connection",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Progress Bar Section
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .offset(y = fadeInUpOffsetY.value.dp)
                    .alpha(fadeInUpAlpha.value)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "INITIALIZING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "${(progress.value * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(50))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
                    )
                }
            }
        }

        // Footer Branding
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .offset(y = fadeInUpOffsetY.value.dp)
                .alpha(fadeInUpAlpha.value * 0.4f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.width(32.dp).height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SOCIO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.width(32.dp).height(1.dp).background(MaterialTheme.colorScheme.outlineVariant))
        }
    }
}
