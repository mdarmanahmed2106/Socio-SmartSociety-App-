package com.smartsociety.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsociety.data.model.Complaint
import com.smartsociety.ui.components.SSCard
import com.smartsociety.ui.components.SSStatusChip
import com.smartsociety.ui.theme.*
import com.smartsociety.viewmodel.AdminComplaintState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminComplaintState: AdminComplaintState,
    onLogoutClick: () -> Unit,
    onComplaintStatusChange: (String, String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedComplaintForDialog by remember { mutableStateOf<Complaint?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Panel", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (adminComplaintState) {
                is AdminComplaintState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is AdminComplaintState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(adminComplaintState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is AdminComplaintState.Success -> {
                    val allComplaints = adminComplaintState.complaints
                    val filteredComplaints = if (selectedFilter == "All") {
                        allComplaints
                    } else {
                        allComplaints.filter { it.status.equals(selectedFilter, ignoreCase = true) }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Text(
                                "Greenwood Residency",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Community Oversight & Issue Management",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            AdminMetricsGrid(allComplaints)
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        item {
                            AdminFilterRow(selectedFilter) { selectedFilter = it }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Recent Complaints", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(filteredComplaints) { complaint ->
                            AdminComplaintItem(
                                complaint = complaint,
                                onManageClick = { selectedComplaintForDialog = complaint }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }

    if (selectedComplaintForDialog != null) {
        StatusChangeDialog(
            complaint = selectedComplaintForDialog!!,
            onDismiss = { selectedComplaintForDialog = null },
            onConfirm = { newStatus ->
                onComplaintStatusChange(selectedComplaintForDialog!!.id, newStatus)
                selectedComplaintForDialog = null
            }
        )
    }
}

@Composable
fun AdminMetricsGrid(complaints: List<Complaint>) {
    val total = complaints.size
    val open = complaints.count { it.status.equals("Open", ignoreCase = true) }
    val inProgress = complaints.count { it.status.equals("In Progress", ignoreCase = true) }
    
    // Simplistic check for resolved today
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val resolvedToday = complaints.count { 
        it.status.equals("Resolved", ignoreCase = true) && it.timestamp >= todayStart 
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(modifier = Modifier.weight(1f), title = "Total", value = total.toString(), subtitle = "Total Reports", icon = Icons.Default.BarChart, iconColor = MaterialTheme.colorScheme.primary)
            MetricCard(modifier = Modifier.weight(1f), title = "Urgent", value = open.toString(), subtitle = "Open Issues", icon = Icons.Default.ReportProblem, iconColor = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(modifier = Modifier.weight(1f), title = "Active", value = inProgress.toString(), subtitle = "In Progress", icon = Icons.Default.Engineering, iconColor = Color(0xFFFF8D69)) // Tertiary container approx
            MetricCard(modifier = Modifier.weight(1f), title = "Today", value = resolvedToday.toString(), subtitle = "Resolved Today", icon = Icons.Default.CheckCircle, iconColor = Color(0xFF00C2A8))
        }
    }
}

@Composable
fun MetricCard(modifier: Modifier, title: String, value: String, subtitle: String, icon: ImageVector, iconColor: Color) {
    Surface(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column {
                Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AdminFilterRow(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf("All", "Open", "In Progress", "Resolved")
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = selected == filter
            Surface(
                modifier = Modifier.clickable { onSelect(filter) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Text(
                    text = filter,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun AdminComplaintItem(complaint: Complaint, onManageClick: () -> Unit) {
    SSCard {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Text(complaint.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("#ISS-${complaint.id.take(4).uppercase()}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                SSStatusChip(complaint.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text("User ID: ${complaint.userId.take(5)}...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(4.dp))
                Text(complaint.location.ifEmpty { "Location not specified" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.outline)
                Spacer(modifier = Modifier.width(4.dp))
                val dateStr = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(Date(complaint.timestamp))
                Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onManageClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Manage Status")
            }
        }
    }
}

@Composable
fun StatusChangeDialog(complaint: Complaint, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var selectedStatus by remember { mutableStateOf(complaint.status) }
    val statuses = listOf("Open", "In Progress", "Resolved")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Status") },
        text = {
            Column {
                Text("Current status for '${complaint.title}' is ${complaint.status}. Select a new status:")
                Spacer(modifier = Modifier.height(16.dp))
                statuses.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedStatus = status }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(status)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(selectedStatus) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
