package com.smartsociety.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartsociety.ui.screens.auth.LoginScreen
import com.smartsociety.ui.screens.auth.RegisterScreen
import com.smartsociety.ui.screens.dashboard.*
import com.smartsociety.ui.screens.profile.NotificationsScreen
import com.smartsociety.ui.screens.profile.ProfileScreen
import com.smartsociety.viewmodel.AdminViewModel
import com.smartsociety.viewmodel.AuthViewModel
import com.smartsociety.viewmodel.ComplaintViewModel
import com.smartsociety.viewmodel.ComplaintState
import com.smartsociety.viewmodel.AuthState
import com.smartsociety.viewmodel.NotificationViewModel
import com.smartsociety.viewmodel.NotificationState

@Composable
fun SocioNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val complaintViewModel: ComplaintViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()
    val complaintState by complaintViewModel.complaintsState.collectAsState()
    val adminComplaintState by adminViewModel.complaintsState.collectAsState()
    val notificationsState by notificationViewModel.notificationsState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            val user = (authState as AuthState.Authenticated).user
            if (user.id != "admin_id") {
                complaintViewModel.fetchComplaints()
                notificationViewModel.fetchNotifications()
            }
        }
    }

    val navigateToTab: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo("dashboard") {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            com.smartsociety.ui.screens.SplashScreen(
                onTimeout = {
                    if (authState is AuthState.Authenticated) {
                        val user = (authState as AuthState.Authenticated).user
                        if (user.id == "admin_id") {
                            navController.navigate("admin_dashboard") {
                                popUpTo("splash") { inclusive = true }
                            }
                        } else {
                            navController.navigate("dashboard") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginClick = { email, pass -> authViewModel.login(email, pass) },
                onRegisterClick = { navController.navigate("register") },
                authState = authState
            )
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    val user = (authState as AuthState.Authenticated).user
                    if (user.id == "admin_id") {
                        navController.navigate("admin_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = { name, email, pass, phone, apartment, address -> 
                    authViewModel.register(name, email, pass, phone, apartment, address) 
                },
                onLoginClick = { navController.navigate("login") },
                authState = authState
            )
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    val user = (authState as AuthState.Authenticated).user
                    if (user.id == "admin_id") {
                        navController.navigate("admin_dashboard") {
                            popUpTo("register") { inclusive = true }
                        }
                    } else {
                        navController.navigate("dashboard") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
            }
        }

        composable("dashboard") {
            val user = (authState as? AuthState.Authenticated)?.user
            
            val complaints = (complaintState as? ComplaintState.Success)?.complaints ?: emptyList()
            val notificationsList = (notificationsState as? NotificationState.Success)?.notifications ?: emptyList()
            val unreadCount = notificationsList.count { !it.isRead }
            
            DashboardScreen(
                userName = user?.name ?: "Resident",
                userApartment = user?.apartment ?: "",
                userAddress = user?.address ?: "",
                complaintState = complaintState,
                unreadNotificationsCount = unreadCount,
                onReportClick = { navController.navigate("report_issue") },
                onComplaintClick = { complaint -> navController.navigate("complaint_detail/${complaint.id}") },
                onMyComplaintsClick = { navigateToTab("my_complaints") },
                onNotificationsClick = { navigateToTab("notifications") },
                onProfileClick = { navigateToTab("profile") }
            )
        }

        composable("admin_dashboard") {
            val residents by adminViewModel.residentsState.collectAsState()
            val announcements by adminViewModel.announcementsState.collectAsState()

            AdminDashboardScreen(
                adminComplaintState = adminComplaintState,
                residentsList = residents,
                announcementsList = announcements,
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("admin_dashboard") { inclusive = true }
                    }
                },
                onComplaintStatusChange = { complaintId, newStatus ->
                    adminViewModel.updateComplaintStatus(complaintId, newStatus)
                },
                onSendAnnouncement = { title, message ->
                    adminViewModel.sendAnnouncement(title, message)
                },
                onComplaintClick = { complaint ->
                    navController.navigate("complaint_detail/${complaint.id}")
                }
            )
        }

        composable("report_issue") {
            val reportState by complaintViewModel.reportState.collectAsState()

            LaunchedEffect(Unit) {
                complaintViewModel.resetReportState()
            }

            LaunchedEffect(reportState) {
                if (reportState is com.smartsociety.viewmodel.ReportIssueState.Success) {
                    navController.popBackStack()
                }
            }

            ReportIssueScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitClick = { title, cat, desc, loc, uri -> 
                    complaintViewModel.reportIssue(title, desc, cat, loc, uri)
                },
                reportState = reportState
            )
        }

        composable("my_complaints") {
            val complaints = (complaintState as? ComplaintState.Success)?.complaints ?: emptyList()
            val notificationsList = (notificationsState as? NotificationState.Success)?.notifications ?: emptyList()
            val unreadCount = notificationsList.count { !it.isRead }
            MyComplaintsScreen(
                complaints = complaints,
                onComplaintClick = { complaint -> navController.navigate("complaint_detail/${complaint.id}") },
                onBackClick = { navController.popBackStack() },
                onDashboardClick = { navigateToTab("dashboard") },
                onNotificationsClick = { navigateToTab("notifications") },
                onProfileClick = { navigateToTab("profile") },
                unreadNotificationsCount = unreadCount
            )
        }

        composable(
            route = "complaint_detail/{complaintId}",
            arguments = listOf(navArgument("complaintId") { type = NavType.StringType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            val complaints = (complaintState as? ComplaintState.Success)?.complaints ?: emptyList()
            val complaint = complaints.find { it.id == complaintId }
            
            if (complaint != null) {
                ComplaintDetailScreen(
                    complaint = complaint,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable("notifications") {
            val list = (notificationsState as? NotificationState.Success)?.notifications ?: emptyList()
            NotificationsScreen(
                notifications = list,
                onBackClick = { navController.popBackStack() },
                onDashboardClick = { navigateToTab("dashboard") },
                onMyComplaintsClick = { navigateToTab("my_complaints") },
                onProfileClick = { navigateToTab("profile") },
                onMarkAllAsReadClick = { notificationViewModel.markAllAsRead() }
            )
        }

        composable("profile") {
            val user = (authState as? AuthState.Authenticated)?.user
            val notificationsList = (notificationsState as? NotificationState.Success)?.notifications ?: emptyList()
            val unreadCount = notificationsList.count { !it.isRead }
            if (user != null) {
                ProfileScreen(
                    user = user,
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = { 
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    },
                    onDashboardClick = { navigateToTab("dashboard") },
                    onMyComplaintsClick = { navigateToTab("my_complaints") },
                    onNotificationsClick = { navigateToTab("notifications") },
                    unreadNotificationsCount = unreadCount
                )
            }
        }
    }
}
