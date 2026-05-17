package com.smartsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsociety.data.model.Complaint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import android.net.Uri
import com.smartsociety.data.firebase.FirebaseManager

sealed class ComplaintState {
    object Loading : ComplaintState()
    data class Success(val complaints: List<Complaint>) : ComplaintState()
    data class Error(val message: String) : ComplaintState()
}

sealed class ReportIssueState {
    object Idle : ReportIssueState()
    object Loading : ReportIssueState()
    object Success : ReportIssueState()
    data class Error(val message: String) : ReportIssueState()
}

class ComplaintViewModel : ViewModel() {
    private val _complaintsState = MutableStateFlow<ComplaintState>(ComplaintState.Loading)
    val complaintsState: StateFlow<ComplaintState> = _complaintsState.asStateFlow()

    private val _reportState = MutableStateFlow<ReportIssueState>(ReportIssueState.Idle)
    val reportState: StateFlow<ReportIssueState> = _reportState.asStateFlow()

    private var fetchJob: kotlinx.coroutines.Job? = null

    fun fetchComplaints() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            FirebaseManager.getComplaintsFlow()
                .catch { e ->
                    e.printStackTrace()
                    _complaintsState.value = ComplaintState.Error("Fetch Error: ${e.message}")
                }
                .collect { complaints ->
                    _complaintsState.value = ComplaintState.Success(complaints)
                }
        }
    }

    fun resetReportState() {
        _reportState.value = ReportIssueState.Idle
    }

    fun reportIssue(title: String, desc: String, category: String, location: String, imageUri: Uri? = null) {
        viewModelScope.launch {
            _reportState.value = ReportIssueState.Loading
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
                _reportState.value = ReportIssueState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _reportState.value = ReportIssueState.Error("Submit Error: ${e.message}")
            }
        }
    }
}
