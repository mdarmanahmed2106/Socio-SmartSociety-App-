package com.smartsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsociety.data.firebase.FirebaseManager
import com.smartsociety.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed class AdminComplaintState {
    object Loading : AdminComplaintState()
    data class Success(val complaints: List<Complaint>) : AdminComplaintState()
    data class Error(val message: String) : AdminComplaintState()
}

class AdminViewModel : ViewModel() {
    private val _complaintsState = MutableStateFlow<AdminComplaintState>(AdminComplaintState.Loading)
    val complaintsState: StateFlow<AdminComplaintState> = _complaintsState.asStateFlow()

    private val _residentsState = MutableStateFlow<List<User>>(emptyList())
    val residentsState: StateFlow<List<User>> = _residentsState.asStateFlow()

    private val _announcementsState = MutableStateFlow<List<Announcement>>(emptyList())
    val announcementsState: StateFlow<List<Announcement>> = _announcementsState.asStateFlow()

    private var fetchJob: kotlinx.coroutines.Job? = null
    private var residentsJob: kotlinx.coroutines.Job? = null
    private var announcementsJob: kotlinx.coroutines.Job? = null

    init {
        fetchAllComplaints()
        fetchResidents()
        fetchAnnouncements()
    }

    fun fetchAllComplaints() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            FirebaseManager.getAllComplaintsFlow()
                .catch { e ->
                    e.printStackTrace()
                    _complaintsState.value = AdminComplaintState.Error("Fetch Error: ${e.message}")
                }
                .collect { complaints ->
                    _complaintsState.value = AdminComplaintState.Success(complaints)
                }
        }
    }

    fun fetchResidents() {
        residentsJob?.cancel()
        residentsJob = viewModelScope.launch {
            FirebaseManager.getAllResidentsFlow()
                .catch { e -> e.printStackTrace() }
                .collect { list -> _residentsState.value = list }
        }
    }

    fun fetchAnnouncements() {
        announcementsJob?.cancel()
        announcementsJob = viewModelScope.launch {
            FirebaseManager.getAnnouncementsFlow()
                .catch { e -> e.printStackTrace() }
                .collect { list -> _announcementsState.value = list }
        }
    }

    fun updateComplaintStatus(complaintId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                FirebaseManager.updateComplaintStatus(complaintId, newStatus)
            } catch (e: Exception) {
                e.printStackTrace()
                // Let the flow handle UI updates, but log the error
            }
        }
    }

    fun sendAnnouncement(title: String, message: String, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            try {
                FirebaseManager.sendBroadcastAnnouncement(title, message)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure(e.message ?: "Failed to send announcement")
            }
        }
    }
}
