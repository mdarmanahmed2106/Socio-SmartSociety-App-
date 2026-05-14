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
            _authState.value = AuthState.Authenticated(
                User(id = currentUser.uid, name = currentUser.displayName ?: "Resident", email = currentUser.email ?: "")
            )
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    _authState.value = AuthState.Authenticated(
                        User(id = firebaseUser.uid, name = firebaseUser.displayName ?: "Resident", email = email)
                    )
                } else {
                    _authState.value = AuthState.Error("Login failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Authentication error")
            }
        }
    }

    fun register(name: String, email: String, pass: String, apartment: String, address: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    // Save additional info to Firestore
                    FirebaseManager.saveUserProfile(firebaseUser.uid, name, email, "", apartment, address)
                    
                    _authState.value = AuthState.Authenticated(
                        User(id = firebaseUser.uid, name = name, email = email, apartment = apartment, address = address)
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration error")
            }
        }
    }

    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }
}
