package com.smartsociety.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartsociety.data.model.Complaint
import com.smartsociety.ui.theme.*
import com.smartsociety.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String,
    userApartment: String,
    userAddress: String,
    complaintState: com.smartsociety.viewmodel.ComplaintState,
    unreadNotificationsCount: Int = 0,
    onReportClick: () -> Unit,
    onComplaintClick: (Complaint) -> Unit,
    onMyComplaintsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val complaints = (complaintState as? com.smartsociety.viewmodel.ComplaintState.Success)?.complaints ?: emptyList()
    Scaffold(
        bottomBar = {
            SSBottomNavigation(
                currentRoute = "dashboard",
                onNavigate = { route ->
                    when (route) {
                        "notifications" -> onNotificationsClick()
                        "profile" -> onProfileClick()
                        "my_complaints" -> onMyComplaintsClick()
                    }
                },
                unreadNotificationsCount = unreadNotificationsCount
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onReportClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Report Issue") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Good Morning,", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userName, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box {
                        IconButton(onClick = onNotificationsClick) {
                            Icon(Icons.Rounded.NotificationsNone, contentDescription = "Notifications", modifier = Modifier.size(28.dp))
                        }
                        // Notification dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .size(8.dp)
                                .background(Color.Red, CircleShape)
                        )
                    }
                }
            }

            // Address Bar
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Apartment, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            if (userAddress.isNotEmpty()) "$userAddress · $userApartment" else "Greenwood Estates · Block B · Unit 402",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Quick Actions 2x2 Grid
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Report Issue",
                        icon = Icons.Rounded.ReportProblem,
                        containerColor = ActionTeal,
                        onClick = onReportClick
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "My Complaints",
                        icon = Icons.AutoMirrored.Rounded.ListAlt,
                        containerColor = ActionIndigo,
                        onClick = onMyComplaintsClick
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Notifications",
                        icon = Icons.Rounded.Notifications,
                        containerColor = ActionSalmon,
                        onClick = onNotificationsClick,
                        badgeCount = unreadNotificationsCount
                    )
                    QuickActionCard(
                        modifier = Modifier.weight(1f),
                        title = "Profile",
                        icon = Icons.Rounded.Person,
                        containerColor = ActionGrey,
                        onClick = onProfileClick
                    )
                }
            }

            // Overview
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text("Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCardSmall(Modifier.weight(1f), "Total Raised", complaints.size.toString(), Color(0xFFBAC3FF), Icons.Rounded.Assessment)
                    StatCardSmall(Modifier.weight(1f), "In Progress", complaints.count { it.status == "In Progress" }.toString(), Color(0xFFFFB300), Icons.Rounded.Autorenew)
                    StatCardSmall(Modifier.weight(1f), "Resolved", complaints.count { it.status == "Resolved" }.toString(), Color(0xFF00E096), Icons.Rounded.CheckCircle)
                }
                
                if (complaintState is com.smartsociety.viewmodel.ComplaintState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error: ${(complaintState as com.smartsociety.viewmodel.ComplaintState.Error).message}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Recent Complaints
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Complaints", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onMyComplaintsClick) {
                        Text("View All", color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(complaints.take(5)) { complaint ->
                SSComplaintItem(complaint, onComplaintClick)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Extra space for FAB
            }
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    containerColor: Color,
    onClick: () -> Unit,
    badgeCount: Int = 0
) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = containerColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                        .background(Color(0xFFFF3B30), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatCardSmall(
    modifier: Modifier,
    title: String,
    value: String,
    valueColor: Color,
    icon: ImageVector
) {
    Surface(
        modifier = modifier
            .border(
                width = 1.dp,
                color = valueColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(valueColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = valueColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                color = valueColor,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

