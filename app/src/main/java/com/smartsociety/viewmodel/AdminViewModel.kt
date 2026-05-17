package com.smartsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsociety.data.firebase.FirebaseManager
import com.smartsociety.data.model.Complaint
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

    private var fetchJob: kotlinx.coroutines.Job? = null

    init {
        fetchAllComplaints()
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
}
