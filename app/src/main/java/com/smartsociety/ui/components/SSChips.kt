package com.smartsociety.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smartsociety.ui.theme.StatusInProgress
import com.smartsociety.ui.theme.StatusOpen
import com.smartsociety.ui.theme.StatusResolved

@Composable
fun SSStatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status.lowercase()) {
        "open" -> StatusOpen
        "in progress" -> StatusInProgress
        "resolved" -> StatusResolved
        else -> MaterialTheme.colorScheme.secondary
    }

    val textColor = if (status.lowercase() == "in progress" || status.lowercase() == "resolved") {
        Color(0xFF0B0F1A) // Dark text for amber/green
    } else {
        Color.White
    }

    Box(
        modifier = modifier
            .background(backgroundColor, MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}
