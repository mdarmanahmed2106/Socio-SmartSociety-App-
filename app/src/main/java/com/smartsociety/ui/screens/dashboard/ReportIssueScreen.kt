package com.smartsociety.ui.screens.dashboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.smartsociety.ui.components.*
import com.smartsociety.ui.theme.*
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportIssueScreen(
    onBackClick: () -> Unit,
    onSubmitClick: (String, String, String, String, Uri?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Water") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Building 4, Sector B, Apartment 402") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var step by remember { mutableIntStateOf(1) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri = uri }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("CivicLink", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) 
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (step > 1) step-- else onBackClick()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
            Spacer(modifier = Modifier.height(16.dp))
            
            FormStepper(currentStep = step)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            when (step) {
                1 -> {
                    Text("Issue Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    SSTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = "Short Title",
                        placeholder = "e.g. Broken elevator"
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    CategoryGrid(selectedCategory) { selectedCategory = it }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Problem Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    SSTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = "",
                        placeholder = "Describe the issue in detail...",
                        singleLine = false,
                        modifier = Modifier.height(120.dp)
                    )
                }
                2 -> {
                    Text("Evidence & Location", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text("Upload Photo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { 
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .size(32.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .clickable { imageUri = null },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Tap to take a photo or upload", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text("Current Location", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    SSTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Location",
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                    )
                }
                3 -> {
                    Text("Review Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    SSCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("TITLE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(title.ifEmpty { "No Title" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                SSStatusChip("Open")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("CATEGORY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(selectedCategory, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("DESCRIPTION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(description.ifEmpty { "No description provided." }, style = MaterialTheme.typography.bodyMedium)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("LOCATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(location, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    
                    if (imageUri != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("EVIDENCE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = { 
                    when (step) {
                        1 -> if (title.isNotEmpty() && description.isNotEmpty()) step = 2
                        2 -> step = 3
                        3 -> onSubmitClick(title, selectedCategory, description, location, imageUri)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (step == 3) ActionIndigo else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = if (step == 3) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                ),
                enabled = when(step) {
                    1 -> title.isNotEmpty() && description.isNotEmpty()
                    else -> true
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (step == 3) "Submit Report" else "Continue to ${if (step == 1) "Upload" else "Review"}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(if (step == 3) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FormStepper(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepItem(1, "Details", currentStep >= 1)
        HorizontalDivider(modifier = Modifier.width(40.dp), color = MaterialTheme.colorScheme.outlineVariant)
        StepItem(2, "Upload", currentStep >= 2)
        HorizontalDivider(modifier = Modifier.width(40.dp), color = MaterialTheme.colorScheme.outlineVariant)
        StepItem(3, "Review", currentStep >= 3)
    }
}

@Composable
fun StepItem(number: Int, label: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(number.toString(), color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun CategoryGrid(selected: String, onSelect: (String) -> Unit) {
    val categories = listOf(
        "Water" to Icons.Default.WaterDrop,
        "Electricity" to Icons.Default.FlashOn,
        "Garbage" to Icons.Default.Delete,
        "Parking" to Icons.Default.LocalParking,
        "Lift" to Icons.Default.Elevator,
        "Security" to Icons.Default.Shield
    )
    
    Column {
        for (i in 0 until categories.size step 3) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (j in i until (i + 3).coerceAtMost(categories.size)) {
                    val cat = categories[j]
                    CategoryItem(
                        modifier = Modifier.weight(1f),
                        title = cat.first,
                        icon = cat.second,
                        isSelected = selected == cat.first,
                        onClick = { onSelect(cat.first) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryItem(modifier: Modifier, title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) ActionTeal else MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.labelMedium, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
