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
import com.smartsociety.data.model.Announcement
import com.smartsociety.data.model.User
import com.smartsociety.ui.components.SSCard
import com.smartsociety.ui.components.SSStatusChip
import com.smartsociety.ui.components.SSTextField
import com.smartsociety.ui.components.SSComplaintItem
import com.smartsociety.ui.theme.*
import com.smartsociety.viewmodel.AdminComplaintState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminComplaintState: AdminComplaintState,
    residentsList: List<User>,
    announcementsList: List<Announcement>,
    onLogoutClick: () -> Unit,
    onComplaintStatusChange: (String, String) -> Unit,
    onSendAnnouncement: (String, String) -> Unit,
    onComplaintClick: (Complaint) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedComplaintForDialog by remember { mutableStateOf<Complaint?>(null) }
    var showAnnouncementDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Panel", 
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.Default.Logout, 
                            contentDescription = "Logout", 
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp,
                modifier = Modifier.height(80.dp)
            ) {
                Column {
                    HorizontalDivider(color = Color(0xFF1E2538), thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxSize()) {
                        val items = listOf(
                            Triple(0, "Dashboard", Icons.Default.Dashboard),
                            Triple(1, "Complaints", Icons.Default.Assignment),
                            Triple(2, "Broadcast", Icons.Default.Campaign),
                            Triple(3, "Residents", Icons.Default.People)
                        )
                        items.forEach { (index, label, icon) ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = { Icon(icon, contentDescription = label) },
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
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 2) {
                ExtendedFloatingActionButton(
                    onClick = { showAnnouncementDialog = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    icon = { Icon(Icons.Default.Announcement, contentDescription = null) },
                    text = { Text("Broadcast") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: DASHBOARD OVERVIEW
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
                                        text = "Prestige",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Community Oversight & Issue Management",
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
                                    Text(
                                        text = "Status Moderation", 
                                        style = MaterialTheme.typography.titleMedium, 
                                        fontWeight = FontWeight.Bold
                                    )
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
                1 -> {
                    // TAB 1: ALL COMPLAINTS (USER SIDE STYLE DIRECTORY)
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
                            AllComplaintsContent(
                                complaints = adminComplaintState.complaints,
                                onComplaintClick = onComplaintClick
                            )
                        }
                    }
                }
                2 -> {
                    // TAB 2: ANNOUNCEMENTS BROADCAST LOG
                    AnnouncementsContent(announcements = announcementsList)
                }
                3 -> {
                    // TAB 3: RESIDENTS DIRECTORY
                    ResidentsContent(residents = residentsList)
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

    if (showAnnouncementDialog) {
        AnnouncementDialog(
            onDismiss = { showAnnouncementDialog = false },
            onConfirm = { title, message ->
                onSendAnnouncement(title, message)
                showAnnouncementDialog = false
            }
        )
    }
}

@Composable
fun AllComplaintsContent(
    complaints: List<Complaint>,
    onComplaintClick: (Complaint) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(complaints, searchQuery) {
        complaints.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "All Complaints",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Browse, search, and click to view full complaint details",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        SSTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search complaints",
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No complaints found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { complaint ->
                    SSComplaintItem(complaint = complaint, onClick = onComplaintClick)
                }
            }
        }
    }
}

@Composable
fun AnnouncementsContent(
    announcements: List<Announcement>
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(announcements, searchQuery) {
        announcements.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.message.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Broadcast Center",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Send and review society-wide announcements",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        SSTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search announcements",
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No announcements found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { announcement ->
                    SSCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = announcement.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(announcement.timestamp))
                                Text(
                                    text = dateStr,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = announcement.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResidentsContent(
    residents: List<User>
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(residents, searchQuery) {
        residents.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.apartment.contains(searchQuery, ignoreCase = true) ||
            it.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Residents Directory",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Contact details for all registered residents",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        SSTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = "Search residents",
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No residents registered yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList) { resident ->
                    SSCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(20.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = resident.name.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = resident.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Apartment: ${resident.apartment}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = resident.phone.ifEmpty { "N/A" },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = resident.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminMetricsGrid(complaints: List<Complaint>) {
    val total = complaints.size
    val open = complaints.count { it.status.equals("Open", ignoreCase = true) }
    val inProgress = complaints.count { it.status.equals("In Progress", ignoreCase = true) }
    
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
            MetricCard(modifier = Modifier.weight(1f), title = "Active", value = inProgress.toString(), subtitle = "In Progress", icon = Icons.Default.Engineering, iconColor = Color(0xFFFF8D69))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Announcement,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "Broadcast Announcement",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "This announcement will be delivered immediately to all Prestige residents.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Announcement Title") },
                    placeholder = { Text("e.g. Water Supply Interruption") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Content") },
                    placeholder = { Text("Enter detail regarding the announcement...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                if (errorText.isNotEmpty()) {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || message.isBlank()) {
                        errorText = "Both title and message are required."
                    } else {
                        onConfirm(title, message)
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Broadcast", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.outline)
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}
