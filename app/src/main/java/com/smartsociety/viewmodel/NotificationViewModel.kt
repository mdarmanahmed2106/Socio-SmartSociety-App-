package com.smartsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsociety.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.smartsociety.data.firebase.FirebaseManager

sealed class NotificationState {
    object Loading : NotificationState()
    data class Success(val notifications: List<Notification>) : NotificationState()
    data class Error(val message: String) : NotificationState()
}

class NotificationViewModel : ViewModel() {
    private val _notificationsState = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val notificationsState: StateFlow<NotificationState> = _notificationsState.asStateFlow()

    init {
        fetchNotifications()
    }

    private var fetchJob: kotlinx.coroutines.Job? = null

    fun fetchNotifications() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            FirebaseManager.getNotificationsFlow()
                .catch { e ->
                    e.printStackTrace()
                    _notificationsState.value = NotificationState.Error("Fetch Error: ${e.message}")
                }
                .collect { notifications ->
                    _notificationsState.value = NotificationState.Success(notifications)
                }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                FirebaseManager.markAllNotificationsAsRead()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
