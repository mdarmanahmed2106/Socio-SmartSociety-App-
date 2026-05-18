package com.smartsociety.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SSBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    unreadNotificationsCount: Int = 0
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        modifier = Modifier.height(80.dp)
    ) {
        // Border Top as per Design MD
        Column {
            HorizontalDivider(color = Color(0xFF1E2538), thickness = 1.dp)
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationItem(
                    selected = currentRoute == "dashboard",
                    onClick = { onNavigate("dashboard") },
                    icon = Icons.Default.Dashboard,
                    label = "Home"
                )
                NavigationItem(
                    selected = currentRoute == "my_complaints",
                    onClick = { onNavigate("my_complaints") },
                    icon = Icons.AutoMirrored.Filled.List,
                    label = "My Issues"
                )
                NavigationItem(
                    selected = currentRoute == "notifications",
                    onClick = { onNavigate("notifications") },
                    icon = Icons.Default.Notifications,
                    label = "Updates",
                    badgeCount = unreadNotificationsCount
                )
                NavigationItem(
                    selected = currentRoute == "profile",
                    onClick = { onNavigate("profile") },
                    icon = Icons.Default.Person,
                    label = "Profile"
                )
            }
        }
    }
}

@Composable
private fun RowScope.NavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    badgeCount: Int = 0
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            if (badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Badge {
                            Text(badgeCount.toString())
                        }
                    }
                ) {
                    Icon(icon, contentDescription = label)
                }
            } else {
                Icon(icon, contentDescription = label)
            }
        },
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
            unselectedIconColor = MaterialTheme.colorScheme.outline,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedTextColor = MaterialTheme.colorScheme.outline,
            indicatorColor = MaterialTheme.colorScheme.primary
        )
    )
}
