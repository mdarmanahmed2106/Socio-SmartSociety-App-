package com.smartsociety.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smartsociety.data.model.Complaint
import com.smartsociety.ui.components.SSTextField

import com.smartsociety.ui.components.SSBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyComplaintsScreen(
    complaints: List<Complaint>,
    onComplaintClick: (Complaint) -> Unit,
    onBackClick: () -> Unit,
    onDashboardClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    unreadNotificationsCount: Int = 0
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Complaints") },
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
                currentRoute = "my_complaints",
                onNavigate = { route ->
                    when (route) {
                        "dashboard" -> onDashboardClick()
                        "notifications" -> onNotificationsClick()
                        "profile" -> onProfileClick()
                    }
                },
                unreadNotificationsCount = unreadNotificationsCount
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            SSTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search complaints",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(complaints.filter { it.title.contains(searchQuery, ignoreCase = true) }) { complaint ->
                    com.smartsociety.ui.components.SSComplaintItem(complaint, onComplaintClick)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}
