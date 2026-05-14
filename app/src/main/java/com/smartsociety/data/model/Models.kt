package com.smartsociety.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val apartment: String = "",
    val address: String = "",
    val profilePic: String = ""
)

data class Complaint(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val status: String = "Open", // Open, In Progress, Resolved
    val timestamp: Long = System.currentTimeMillis(),
    val location: String = "",
    val imageUri: String = ""
)

data class Notification(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
