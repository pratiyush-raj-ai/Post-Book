package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.InstagramGradient
import com.example.ui.theme.InstaPink
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val authState by viewModel.uiState.collectAsStateWithLifecycle()

    var isSignUp by remember { mutableStateOf(false) }
    var identifier by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("+1 (555) 019-2834") }
    var otpCode by remember { mutableStateOf("") }
    var showPhoneAuthDialog by remember { mutableStateOf(false) }

    // Handle authentication success
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }

    val isLoading = authState is AuthUiState.Loading

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Branding Title
            Text(
                text = "InstaVibe",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = com.example.ui.theme.AppLogoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 46.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Text(
                text = "Capture. Create. Connect.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
            )

            // Auth Mode Toggle (Log In / Sign Up)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .then(
                                if (!isSignUp) Modifier.background(InstagramGradient)
                                else Modifier
                            )
                            .clickable {
                                isSignUp = false
                                viewModel.resetState()
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "Log In",
                            fontWeight = FontWeight.Bold,
                            color = if (!isSignUp) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .then(
                                if (isSignUp) Modifier.background(InstagramGradient)
                                else Modifier
                            )
                            .clickable {
                                isSignUp = true
                                viewModel.resetState()
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = "Sign Up",
                            fontWeight = FontWeight.Bold,
                            color = if (isSignUp) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Username Input
            OutlinedTextField(
                value = identifier,
                onValueChange = {
                    identifier = it
                    if (authState is AuthUiState.Error) viewModel.resetState()
                },
                label = { Text("Username or Handle") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_username_input")
            )

            if (isSignUp) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (authState is AuthUiState.Error) viewModel.resetState()
                    },
                    label = { Text("Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_email_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (authState is AuthUiState.Error) viewModel.resetState()
                },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_password_input")
            )

            // Display Error Message if any
            if (authState is AuthUiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (authState as AuthUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Action Button (Log In or Sign Up via ViewModel)
            Button(
                onClick = {
                    if (isSignUp) {
                        viewModel.register(identifier, email, password)
                    } else {
                        viewModel.login(identifier, password)
                    }
                },
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = InstaPink),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("auth_primary_button")
            ) {
                if (isLoading && authState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isSignUp) "Create Account" else "Log In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section for new account with Google / Apple / Mobile login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Text(
                    text = if (isSignUp) "Or register with" else "Or quick connect with",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Buttons Row (Google, Apple, Mobile Phone)
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Google Login Button (via ViewModel + Credential Manager)
                OutlinedButton(
                    onClick = {
                        activity?.let { act ->
                            viewModel.signInWithGoogle(act)
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("auth_google_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Google", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                // Apple Login Button
                OutlinedButton(
                    onClick = {
                        viewModel.login("alex_apple", "apple123")
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("auth_apple_button")
                ) {
                    Text("Apple", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                // Mobile Number Login Button (via ViewModel + Firebase PhoneAuth)
                OutlinedButton(
                    onClick = { showPhoneAuthDialog = true },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("auth_phone_button")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Mobile Login", modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Demo 1-Tap Access for Instant Evaluation
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.loginDemo()
                    }
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .testTag("auth_demo_access_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🚀 Quick Demo Mode (Alex Rivers)",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = InstaPink
                        )
                    )
                }
            }
        }

        // Phone Authentication Entry Dialog
        if (showPhoneAuthDialog) {
            AlertDialog(
                onDismissRequest = { showPhoneAuthDialog = false },
                title = { Text("Sign In with Mobile Number") },
                text = {
                    Column {
                        Text(
                            text = "Enter your mobile phone number with country code to receive an SMS verification code via Firebase.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = { Text("+1 (555) 019-2834") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showPhoneAuthDialog = false
                            activity?.let { act ->
                                viewModel.startPhoneVerification(act, phoneNumber)
                            }
                        }
                    ) {
                        Text("Send SMS Code")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPhoneAuthDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // SMS OTP Verification Dialog (when code is sent by Firebase)
        val currentOtpState = authState as? AuthUiState.OtpSent
        if (currentOtpState != null) {
            AlertDialog(
                onDismissRequest = { viewModel.resetState() },
                title = { Text("Enter Verification Code") },
                text = {
                    Column {
                        Text(
                            text = "Enter the 6-digit code sent to ${currentOtpState.phoneNumber}:",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { if (it.length <= 6) otpCode = it },
                            placeholder = { Text("123456") },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.verifyOtpCode(
                                verificationId = currentOtpState.verificationId,
                                phoneNumber = currentOtpState.phoneNumber,
                                code = otpCode
                            )
                        },
                        enabled = otpCode.length >= 6
                    ) {
                        Text("Verify & Sign In")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.resetState() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
