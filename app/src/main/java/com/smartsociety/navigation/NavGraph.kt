package com.smartsociety.navigation

import androidx.compose.runtime.Composable
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
import com.smartsociety.viewmodel.AuthViewModel
import com.smartsociety.viewmodel.ComplaintViewModel
import com.smartsociety.viewmodel.ComplaintState
import com.smartsociety.viewmodel.AuthState

@Composable
fun SmartSocietyNavGraph() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val complaintViewModel: ComplaintViewModel = viewModel()

    val authState by authViewModel.authState.collectAsState()
    val complaintState by complaintViewModel.complaintsState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = { email, pass -> authViewModel.login(email, pass) },
                onRegisterClick = { navController.navigate("register") }
            )
            if (authState is AuthState.Authenticated) {
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        composable("register") {
            RegisterScreen(
                onRegisterClick = { name, email, pass, apartment, address -> 
                    authViewModel.register(name, email, pass, apartment, address) 
                },
                onLoginClick = { navController.navigate("login") }
            )
            if (authState is AuthState.Authenticated) {
                navController.navigate("dashboard") {
                    popUpTo("register") { inclusive = true }
                }
            }
        }

        composable("dashboard") {
            val user = (authState as? AuthState.Authenticated)?.user
            val complaints = (complaintState as? ComplaintState.Success)?.complaints ?: emptyList()
            
            DashboardScreen(
                userName = user?.name ?: "Resident",
                userApartment = user?.apartment ?: "",
                userAddress = user?.address ?: "",
                complaints = complaints,
                onReportClick = { navController.navigate("report_issue") },
                onComplaintClick = { complaint -> navController.navigate("complaint_detail/${complaint.id}") },
                onNotificationsClick = { navController.navigate("notifications") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        composable("report_issue") {
            ReportIssueScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitClick = { title, cat, desc, loc, uri -> 
                    complaintViewModel.reportIssue(title, desc, cat, loc, uri)
                    navController.popBackStack()
                }
            )
        }

        composable("my_complaints") {
            val complaints = (complaintState as? ComplaintState.Success)?.complaints ?: emptyList()
            MyComplaintsScreen(
                complaints = complaints,
                onComplaintClick = { complaint -> navController.navigate("complaint_detail/${complaint.id}") },
                onBackClick = { navController.popBackStack() }
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
            NotificationsScreen(
                notifications = emptyList(), // Dummy
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("profile") {
            val user = (authState as? AuthState.Authenticated)?.user
            if (user != null) {
                ProfileScreen(
                    user = user,
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = { 
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("dashboard") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
