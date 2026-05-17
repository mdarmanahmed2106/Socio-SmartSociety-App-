package com.smartsociety.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsociety.data.model.Complaint
import com.smartsociety.ui.components.SSStatusChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(
    complaint: Complaint,
    onBackClick: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val receivedTime = remember(complaint.timestamp) {
        try {
            sdf.format(Date(complaint.timestamp))
        } catch (e: Exception) {
            "Unknown"
        }
    }
    val assignedTime = remember(complaint.timestamp, complaint.status) {
        try {
            if (complaint.status != "Open") {
                sdf.format(Date(complaint.timestamp + 2 * 3600 * 1000))
            } else {
                "Pending assignment"
            }
        } catch (e: Exception) {
            "Pending assignment"
        }
    }
    val inProgressTime = remember(complaint.timestamp, complaint.status) {
        try {
            if (complaint.status == "In Progress" || complaint.status == "Resolved") {
                sdf.format(Date(complaint.timestamp + 4 * 3600 * 1000))
            } else {
                "Pending"
            }
        } catch (e: Exception) {
            "Pending"
        }
    }
    val resolvedTime = remember(complaint.timestamp, complaint.status) {
        try {
            if (complaint.status == "Resolved") {
                sdf.format(Date(complaint.timestamp + 24 * 3600 * 1000))
            } else {
                "Not yet"
            }
        } catch (e: Exception) {
            "Not yet"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaint Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(complaint.category, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                SSStatusChip(complaint.status)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(complaint.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(complaint.location.ifEmpty { "General Area" }, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Description", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(complaint.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            Text("Timeline", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            
            TimelineItem("Complaint Received", receivedTime, true)
            TimelineItem("Assigned to Technician", assignedTime, complaint.status != "Open")
            TimelineItem("Work in Progress", inProgressTime, complaint.status == "In Progress" || complaint.status == "Resolved")
            TimelineItem("Resolved", resolvedTime, complaint.status == "Resolved")
        }
    }
}

@Composable
fun TimelineItem(title: String, subtitle: String, isCompleted: Boolean) {
    Row(modifier = Modifier.padding(bottom = 16.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(2.dp)
                    .background(
                        if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        androidx.compose.foundation.shape.CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
