package com.smartsociety.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.smartsociety.ui.components.SSPrimaryButton
import com.smartsociety.ui.components.SSTextField
import com.smartsociety.viewmodel.AuthState

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    authState: AuthState = AuthState.Idle
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var apartment by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Join SmartSociety",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Create an account to start reporting issues",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        SSTextField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SSTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SSTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone Number",
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SSTextField(
            value = apartment,
            onValueChange = { apartment = it },
            label = "Apartment Number (e.g. B-402)",
            leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SSTextField(
            value = address,
            onValueChange = { address = it },
            label = "Society / Address",
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SSTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        SSTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (authState is AuthState.Error) {
            Text(
                text = authState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        SSPrimaryButton(
            text = if (authState is AuthState.Loading) "Registering..." else "Register",
            onClick = { 
                if (password == confirmPassword) {
                    onRegisterClick(name.trim(), email.trim(), password.trim(), phone.trim(), apartment.trim(), address.trim())
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Already have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onLoginClick) {
                Text("Login", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}
