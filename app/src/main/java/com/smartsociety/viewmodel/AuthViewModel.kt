package com.smartsociety.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartsociety.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.smartsociety.data.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val user = FirebaseManager.getUserProfile(currentUser.uid)
                    _authState.value = AuthState.Authenticated(user)
                } catch (e: Exception) {
                    _authState.value = AuthState.Authenticated(
                        User(id = currentUser.uid, name = currentUser.displayName ?: "Resident", email = currentUser.email ?: "")
                    )
                }
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Mock Admin Login
            if (email == "admin@society.com" && pass == "admin123") {
                _authState.value = AuthState.Authenticated(
                    User(id = "admin_id", name = "Administrator", email = email)
                )
                return@launch
            }
            
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = FirebaseManager.getUserProfile(firebaseUser.uid)
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Error("Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication error")
            }
        }
    }

    fun register(name: String, email: String, pass: String, phone: String, apartment: String, address: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    // Save additional info to Firestore
                    FirebaseManager.saveUserProfile(firebaseUser.uid, name, email, phone, apartment, address)
                    // Seed mock notifications
                    FirebaseManager.seedMockNotifications(firebaseUser.uid)
                    
                    _authState.value = AuthState.Authenticated(
                        User(id = firebaseUser.uid, name = name, email = email, phone = phone, apartment = apartment, address = address)
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration error")
            }
        }
    }

    fun logout() {
        if (_authState.value is AuthState.Authenticated) {
            val user = (_authState.value as AuthState.Authenticated).user
            if (user.id == "admin_id") {
                _authState.value = AuthState.Idle
                return
            }
        }
        
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}
