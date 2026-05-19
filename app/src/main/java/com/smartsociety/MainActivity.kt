package com.smartsociety

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.smartsociety.navigation.SocioNavGraph
import com.smartsociety.ui.theme.SocioTheme
import com.smartsociety.util.RealtimeNotificationListener

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, start listening if user is authenticated
            val currentUser = try { FirebaseAuth.getInstance().currentUser } catch (e: Exception) { null }
            if (currentUser != null) {
                RealtimeNotificationListener.startListening(applicationContext)
            }
        }
    }

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            RealtimeNotificationListener.startListening(applicationContext)
        } else {
            RealtimeNotificationListener.stopListening()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition on Android 12+
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Request runtime permission for notifications on Android 13+
        checkAndRequestNotificationPermission()

        // Setup real-time listener active when user is authenticated
        try {
            FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            SocioTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    SocioNavGraph()
                }
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Stop listener to avoid leaking background tasks
        RealtimeNotificationListener.stopListening()
    }
}

