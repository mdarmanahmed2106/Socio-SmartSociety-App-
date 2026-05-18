package com.smartsociety.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartsociety.data.model.Notification
import com.smartsociety.ui.components.SSBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<Notification>,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onMyComplaintsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMarkAllAsReadClick: () -> Unit
) {
    val groupedNotifications = remember(notifications) { groupNotifications(notifications) }
    val unreadCount = remember(notifications) { notifications.count { !it.isRead } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            SSBottomNavigation(
                currentRoute = "notifications",
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> onDashboardClick()
                        "my_complaints" -> onMyComplaintsClick()
                        "profile" -> onProfileClick()
                    }
                },
                unreadNotificationsCount = unreadCount
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            // Premium Header Block matching notifications.png
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Notifications",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Stay updated with community changes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "Mark all as read",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onMarkAllAsReadClick() }
                        .padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notifications yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    val categories = listOf("Today", "Yesterday", "Earlier")
                    categories.forEach { category ->
                        val list = groupedNotifications[category] ?: emptyList()
                        if (list.isNotEmpty()) {
                            item {
                                Text(
                                    text = category,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                            items(list) { notification ->
                                NotificationItem(notification)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    val borderColor = when {
        notification.title.contains("Resolved", ignoreCase = true) -> Color(0xFF00E096)
        notification.title.contains("Progress", ignoreCase = true) || notification.title.contains("Update", ignoreCase = true) -> Color(0xFFFFB300)
        notification.title.contains("Assigned", ignoreCase = true) -> Color(0xFFBAC3FF)
        else -> Color(0xFFBAC3FF)
    }

    val icon = when {
        notification.title.contains("Resolved", ignoreCase = true) -> Icons.Default.CheckCircle
        notification.title.contains("Progress", ignoreCase = true) || notification.title.contains("Update", ignoreCase = true) -> Icons.Default.History
        notification.title.contains("Assigned", ignoreCase = true) -> Icons.Default.Person
        else -> Icons.Default.Info
    }

    val iconBgColor = borderColor.copy(alpha = 0.15f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left indicator bar matching notifications.png
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                verticalAlignment = Alignment.Top
            ) {
                // Icon box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(iconBgColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = borderColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Text details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatNotificationTime(notification.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                // Unread dot
                if (!notification.isRead) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.CenterVertically)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }
            }
        }
    }
}

fun groupNotifications(notifications: List<Notification>): Map<String, List<Notification>> {
    val grouped = mutableMapOf<String, MutableList<Notification>>()
    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L
    
    for (notif in notifications) {
        val diff = now - notif.timestamp
        val category = when {
            diff < oneDayMs -> "Today"
            diff < 2 * oneDayMs -> "Yesterday"
            else -> "Earlier"
        }
        grouped.getOrPut(category) { mutableListOf() }.add(notif)
    }
    return grouped
}

fun formatNotificationTime(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
    val dateStr = sdf.format(java.util.Date(timestamp))
    
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val oneDayMs = 24 * 60 * 60 * 1000L
    
    return when {
        diff < oneDayMs -> dateStr
        diff < 2 * oneDayMs -> "Yesterday, $dateStr"
        else -> {
            val fullSdf = java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())
            fullSdf.format(java.util.Date(timestamp))
        }
    }
}
