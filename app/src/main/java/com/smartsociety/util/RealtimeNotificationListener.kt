package com.smartsociety.util

import android.content.Context
import com.smartsociety.data.firebase.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

object RealtimeNotificationListener {
    private var listenerJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var sessionStartTime: Long = 0
    private val shownNotificationIds = mutableSetOf<String>()

    /**
     * Starts listening to the Firestore real-time notification flow.
     * Only schedules notification alerts for records added after the flow is activated.
     */
    fun startListening(context: Context) {
        // Prevent starting multiple duplicate listeners
        if (listenerJob != null) return

        sessionStartTime = System.currentTimeMillis()
        shownNotificationIds.clear()

        listenerJob = coroutineScope.launch {
            FirebaseManager.getNotificationsFlow()
                .catch { e ->
                    e.printStackTrace()
                }
                .collect { notifications ->
                    // Iterate and display new, un-notified documents
                    for (notification in notifications) {
                        val isNew = notification.timestamp >= (sessionStartTime - 5000) // 5 seconds grace period
                        val isAlreadyShown = shownNotificationIds.contains(notification.id)

                        if (isNew && !isAlreadyShown && notification.id.isNotEmpty()) {
                            shownNotificationIds.add(notification.id)
                            NotificationHelper.showNotification(
                                context = context,
                                title = notification.title,
                                message = notification.message
                            )
                        }
                    }
                }
        }
    }

    /**
     * Stops the active Firestore real-time listener and releases resources.
     */
    fun stopListening() {
        listenerJob?.cancel()
        listenerJob = null
        shownNotificationIds.clear()
    }
}
