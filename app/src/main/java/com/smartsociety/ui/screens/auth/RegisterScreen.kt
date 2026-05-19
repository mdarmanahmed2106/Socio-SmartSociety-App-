package com.smartsociety.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var localError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E1513))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Branding Section Logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF00C2A8), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = null,
                    tint = Color(0xFF00382F),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Socio",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF42DEC3),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Onboarding Header Left-Aligned
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDDE4E1),
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Join our community to start reporting issues.",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFBBCAC4).copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Glassmorphism Registration Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = Color(0xFF3C4A46).copy(alpha = 0.3f),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .background(
                        color = Color(0xFF161D1B).copy(alpha = 0.85f),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Full Name Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "FULL NAME",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDDE4E1).copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SSAuthTextField(
                            value = name,
                            onValueChange = { 
                                name = it
                                localError = null
                            },
                            placeholder = "John Doe",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF85948F)
                                )
                            }
                        )
                    }

                    // Email Address Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EMAIL ADDRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDDE4E1).copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SSAuthTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                localError = null
                            },
                            placeholder = "name@example.com",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF85948F)
                                )
                            }
                        )
                    }

                    // Phone Number Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "PHONE NUMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDDE4E1).copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SSAuthTextField(
                            value = phone,
                            onValueChange = { 
                                phone = it
                                localError = null
                            },
                            placeholder = "1234567890",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color(0xFF85948F)
                                )
                            }
                        )
                    }

                    // Apartment Number Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "APARTMENT NUMBER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDDE4E1).copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SSAuthTextField(
                            value = apartment,
                            onValueChange = { 
                                apartment = it
                                localError = null
                            },
                            placeholder = "B-402",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = null,
                                    tint = Color(0xFF85948F)
                                )
                            }
                        )
                    }

                    // Society / Address Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "SOCIETY / ADDRESS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDDE4E1).copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SSAuthTextField(
                            value = address,
                            onValueChange = { 
                                address = it
                                localError = null
                            },
                            placeholder = "Greenwood Residency",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF85948F)
                                )
                            }
                        )
                    }

                    // Password Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDDE4E1).copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SSAuthTextField(
                            value = password,
                            onValueChange = { 
                                password = it
                                localError = null
                            },
                            placeholder = "••••••••",
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF85948F)
                                )
                            },
                            trailingIcon = {
                                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = Color(0xFF85948F)
                                    )
                                }
                            }
                        )
                    }

                    // Confirm Password Section
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "CONFIRM PASSWORD",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDDE4E1).copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SSAuthTextField(
                            value = confirmPassword,
                            onValueChange = { 
                                confirmPassword = it
                                localError = null
                            },
                            placeholder = "••••••••",
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color(0xFF85948F)
                                )
                            },
                            trailingIcon = {
                                val icon = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = Color(0xFF85948F)
                                    )
                                }
                            }
                        )
                    }

                    // Validation Errors
                    val displayedError = localError ?: (if (authState is AuthState.Error) authState.message else null)
                    if (displayedError != null) {
                        Text(
                            text = displayedError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Action Register Button
                    Button(
                        onClick = { 
                            localError = null
                            if (name.isBlank() || email.isBlank() || phone.isBlank() || apartment.isBlank() || address.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                localError = "All fields are required"
                            } else if (password != confirmPassword) {
                                localError = "Passwords do not match"
                            } else if (password.length < 6) {
                                localError = "Password must be at least 6 characters"
                            } else {
                                onRegisterClick(name.trim(), email.trim(), password.trim(), phone.trim(), apartment.trim(), address.trim())
                            }
                        },
                        enabled = authState !is AuthState.Loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00C2A8),
                            contentColor = Color(0xFF00382F),
                            disabledContainerColor = Color(0xFF00C2A8).copy(alpha = 0.5f),
                            disabledContentColor = Color(0xFF00382F).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (authState is AuthState.Loading) "Registering..." else "Register",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            if (authState !is AuthState.Loading) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Login Footer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFBBCAC4)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        TextButton(
                            onClick = onLoginClick,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "Login",
                                color = Color(0xFF42DEC3),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
