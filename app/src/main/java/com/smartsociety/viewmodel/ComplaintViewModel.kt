package com.smartsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsociety.data.model.Complaint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.Uri
import com.smartsociety.data.firebase.FirebaseManager

sealed class ComplaintState {
    object Loading : ComplaintState()
    data class Success(val complaints: List<Complaint>) : ComplaintState()
    data class Error(val message: String) : ComplaintState()
}

class ComplaintViewModel : ViewModel() {
    private val _complaintsState = MutableStateFlow<ComplaintState>(ComplaintState.Loading)
    val complaintsState: StateFlow<ComplaintState> = _complaintsState.asStateFlow()

    init {
        fetchComplaints()
    }

    fun fetchComplaints() {
        viewModelScope.launch {
            _complaintsState.value = ComplaintState.Loading
            try {
                val complaints = FirebaseManager.getComplaints()
                _complaintsState.value = ComplaintState.Success(complaints)
            } catch (e: Exception) {
                _complaintsState.value = ComplaintState.Error(e.message ?: "Failed to fetch complaints")
            }
        }
    }

    fun reportIssue(title: String, desc: String, category: String, location: String, imageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                val newComplaint = Complaint(
                    id = "",
                    userId = FirebaseManager.getCurrentUserId() ?: "",
                    title = title,
                    description = desc,
                    category = category,
                    location = location,
                    status = "Open"
                )
                FirebaseManager.submitComplaint(newComplaint, imageUri)
                fetchComplaints() // Refresh list
            } catch (e: Exception) {
                _complaintsState.value = ComplaintState.Error(e.message ?: "Failed to report issue")
            }
        }
    }
}
