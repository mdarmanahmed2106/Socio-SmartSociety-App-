package com.smartsociety.data.firebase

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.smartsociety.data.model.Complaint
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
        firestore.collection("complaints").document(complaintId)
            .update("status", newStatus).await()
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
