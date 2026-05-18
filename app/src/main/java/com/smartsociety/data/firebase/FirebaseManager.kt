package com.smartsociety.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smartsociety.data.model.Complaint
import com.smartsociety.data.model.User
import com.smartsociety.data.model.Notification
import com.smartsociety.data.model.Announcement
import kotlinx.coroutines.tasks.await
import java.util.UUID

import kotlinx.coroutines.channels.awaitClose

object FirebaseManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Auth
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Firestore - Users
    suspend fun saveUserProfile(userId: String, name: String, email: String, phone: String, apartment: String, address: String) {
        val userMap = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "apartment" to apartment,
            "address" to address,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(userId).set(userMap).await()
    }

    suspend fun getUserProfile(userId: String): User {
        val doc = firestore.collection("users").document(userId).get().await()
        return User(
            id = userId,
            name = doc.getString("name") ?: "",
            email = doc.getString("email") ?: "",
            phone = doc.getString("phone") ?: "",
            apartment = doc.getString("apartment") ?: "",
            address = doc.getString("address") ?: "",
            profilePic = doc.getString("profilePic") ?: ""
        )
    }

    // Firestore - Complaints
    suspend fun submitComplaint(complaint: Complaint, imageUri: Uri? = null) {
        val userId = getCurrentUserId() ?: throw Exception("User not logged in")
        var finalImageUrl = ""

        if (imageUri != null) {
            finalImageUrl = uploadImage(imageUri)
        }

        val complaintData = mapOf(
            "userId" to userId,
            "title" to complaint.title,
            "category" to complaint.category,
            "description" to complaint.description,
            "status" to complaint.status,
            "location" to complaint.location,
            "imageUrl" to finalImageUrl,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("complaints").add(complaintData).await()
    }

    suspend fun getComplaints(): List<Complaint> {
        val userId = getCurrentUserId() ?: return emptyList()
        val snapshot = firestore.collection("complaints")
            .whereEqualTo("userId", userId)
            .get()
            .await()
        
        return snapshot.documents.map { doc ->
            Complaint(
                id = doc.id,
                userId = doc.getString("userId") ?: "",
                title = doc.getString("title") ?: "",
                description = doc.getString("description") ?: "",
                category = doc.getString("category") ?: "",
                status = doc.getString("status") ?: "Open",
                location = doc.getString("location") ?: "",
                imageUri = doc.getString("imageUrl") ?: ""
            )
        }
    }

    fun getComplaintsFlow(): kotlinx.coroutines.flow.Flow<List<Complaint>> = kotlinx.coroutines.flow.callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("complaints")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val complaints = snapshot.documents.map { doc ->
                        Complaint(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            category = doc.getString("category") ?: "",
                            status = doc.getString("status") ?: "Open",
                            location = doc.getString("location") ?: "",
                            imageUri = doc.getString("imageUrl") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    }.sortedByDescending { it.timestamp }
                    trySend(complaints)
                }
            }
        awaitClose { listener.remove() }
    }

    // Admin Flow
    fun getAllComplaintsFlow(): kotlinx.coroutines.flow.Flow<List<Complaint>> = kotlinx.coroutines.flow.callbackFlow {
        val listener = firestore.collection("complaints")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val complaints = snapshot.documents.map { doc ->
                        Complaint(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            category = doc.getString("category") ?: "",
                            status = doc.getString("status") ?: "Open",
                            location = doc.getString("location") ?: "",
                            imageUri = doc.getString("imageUrl") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    }.sortedByDescending { it.timestamp }
                    trySend(complaints)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateComplaintStatus(complaintId: String, newStatus: String) {
        val docRef = firestore.collection("complaints").document(complaintId)
        val snapshot = docRef.get().await()
        val userId = snapshot.getString("userId") ?: ""
        val complaintTitle = snapshot.getString("title") ?: "Complaint"
        
        docRef.update("status", newStatus).await()
        
        if (userId.isNotEmpty()) {
            val title = when (newStatus) {
                "In Progress" -> "Issue In Progress"
                "Resolved" -> "Issue Resolved"
                else -> "Issue Status Update"
            }
            val message = when (newStatus) {
                "In Progress" -> "Your report for '$complaintTitle' is now in progress. A technician has been assigned."
                "Resolved" -> "Your report for '$complaintTitle' has been marked as resolved. Thank you for your patience!"
                else -> "Your report for '$complaintTitle' has been updated to $newStatus."
            }
            val notificationData = mapOf(
                "userId" to userId,
                "title" to title,
                "message" to message,
                "isRead" to false,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("notifications").add(notificationData).await()
        }
    }

    suspend fun markAllNotificationsAsRead() {
        val userId = getCurrentUserId() ?: return
        val snapshot = firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get().await()
        if (!snapshot.isEmpty) {
            firestore.runBatch { batch ->
                for (doc in snapshot.documents) {
                    batch.update(doc.reference, "isRead", true)
                }
            }.await()
        }
    }

    // Notifications
    fun getNotificationsFlow(): kotlinx.coroutines.flow.Flow<List<Notification>> = kotlinx.coroutines.flow.callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = firestore.collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        Notification(
                            id = doc.id,
                            userId = doc.getString("userId") ?: "",
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            isRead = doc.getBoolean("isRead") ?: false,
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    }.sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendBroadcastAnnouncement(title: String, message: String) {
        val announcementRef = firestore.collection("announcements").document()
        val announcementData = mapOf(
            "title" to title,
            "message" to message,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("announcements").document(announcementRef.id).set(announcementData).await()

        val snapshot = firestore.collection("users").get().await()
        if (!snapshot.isEmpty) {
            firestore.runBatch { batch ->
                for (doc in snapshot.documents) {
                    val userId = doc.id
                    val notifRef = firestore.collection("notifications").document()
                    val notifData = mapOf(
                        "userId" to userId,
                        "title" to "📢 Announcement: $title",
                        "message" to message,
                        "isRead" to false,
                        "timestamp" to System.currentTimeMillis()
                    )
                    batch.set(notifRef, notifData)
                }
            }.await()
        }
    }

    fun getAnnouncementsFlow(): kotlinx.coroutines.flow.Flow<List<Announcement>> = kotlinx.coroutines.flow.callbackFlow {
        val listener = firestore.collection("announcements")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        Announcement(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            message = doc.getString("message") ?: "",
                            timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                        )
                    }.sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    fun getAllResidentsFlow(): kotlinx.coroutines.flow.Flow<List<User>> = kotlinx.coroutines.flow.callbackFlow {
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.map { doc ->
                        User(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            email = doc.getString("email") ?: "",
                            phone = doc.getString("phone") ?: "",
                            apartment = doc.getString("apartment") ?: "",
                            address = doc.getString("address") ?: "",
                            profilePic = doc.getString("profilePic") ?: ""
                        )
                    }.sortedBy { it.name }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun seedMockNotifications(userId: String) {
        val mock1 = mapOf(
            "userId" to userId,
            "title" to "Welcome to SmartSociety!",
            "message" to "We are thrilled to have you here. You can now start reporting complaints or monitoring community announcements.",
            "isRead" to false,
            "timestamp" to System.currentTimeMillis()
        )
        val mock2 = mapOf(
            "userId" to userId,
            "title" to "Maintenance Announcement",
            "message" to "Please note that lift maintenance in Block B is scheduled for tomorrow between 10 AM and 1 PM.",
            "isRead" to false,
            "timestamp" to System.currentTimeMillis() - 3600000 // 1 hour ago
        )
        firestore.collection("notifications").add(mock1).await()
        firestore.collection("notifications").add(mock2).await()
    }

    // Storage
    private suspend fun uploadImage(uri: Uri): String {
        return try {
            val fileName = UUID.randomUUID().toString()
            val ref = storage.reference.child("complaints/$fileName")
            
            // Wait for the upload to complete
            ref.putFile(uri).await()
            
            // Then get the download URL
            val downloadUri = ref.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw java.lang.Exception("${e.message}. Ensure Firebase Storage is initialized and rules allow uploads.")
        }
    }
}
