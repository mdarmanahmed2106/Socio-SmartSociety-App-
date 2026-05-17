package com.smartsociety.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.smartsociety.data.model.Complaint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SSComplaintItem(
    complaint: Complaint,
    onClick: (Complaint) -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(complaint.timestamp) {
        try {
            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            sdf.format(Date(complaint.timestamp))
        } catch (e: Exception) {
            "Unknown Date"
        }
    }

    SSCard(modifier = modifier.clickable { onClick(complaint) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when(complaint.category) {
                            "Water" -> Icons.Default.WaterDrop
                            "Electricity" -> Icons.Default.FlashOn
                            else -> Icons.Default.Report
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(complaint.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(complaint.location.ifEmpty { "General Area" }, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SSStatusChip(complaint.status)
                Text(formattedDate, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
