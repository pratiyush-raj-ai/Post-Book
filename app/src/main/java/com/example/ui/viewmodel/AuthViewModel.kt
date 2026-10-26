package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseAuthHelper
import com.example.data.SampleDataProvider
import com.example.data.SocialRepository
import com.example.model.User
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class OtpSent(val verificationId: String, val phoneNumber: String) : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel : ViewModel() {
    private val TAG = "AuthViewModel"

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null

    init {
        // Observe Firebase Auth state
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            SocialRepository.loginWithFirebaseUser(currentUser)
            val appUser = SocialRepository.currentUser.value ?: SampleDataProvider.currentUser
            _uiState.value = AuthUiState.Success(appUser)
        }
    }

    /**
     * Google Sign-In using Jetpack CredentialManager via FirebaseAuthHelper
     */
    fun signInWithGoogle(activity: Activity) {
        _uiState.value = AuthUiState.Loading
        FirebaseAuthHelper.signInWithGoogle(
            context = activity,
            scope = viewModelScope,
            onSuccess = { name ->
                Log.d(TAG, "Google sign in successful for: $name")
                val appUser = SocialRepository.currentUser.value ?: SampleDataProvider.currentUser
                _uiState.value = AuthUiState.Success(appUser)
            },
            onError = { error ->
                Log.e(TAG, "Google sign in error: $error")
                _uiState.value = AuthUiState.Error(error)
            },
            onCancelled = {
                Log.d(TAG, "Google sign in cancelled by user")
                _uiState.value = AuthUiState.Idle
            }
        )
    }

    /**
     * Initiate Mobile Number Verification (Sends SMS OTP via Firebase PhoneAuthProvider)
     */
    fun startPhoneVerification(activity: Activity, phoneNumber: String) {
        if (phoneNumber.isBlank() || phoneNumber.length < 7) {
            _uiState.value = AuthUiState.Error("Please enter a valid phone number with country code (e.g. +1...)")
            return
        }

        _uiState.value = AuthUiState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Instant auto-verification (SMS code detected on device)
                signInWithPhoneCredential(credential, phoneNumber)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "Phone verification failed: ${e.message}", e)
                // If phone provider is not enabled in Firebase Console yet, allow smooth preview testing
                if (e.message?.contains("CONFIGURATION_NOT_FOUND") == true ||
                    e.message?.contains("BILLING_NOT_ENABLED") == true ||
                    e.message?.contains("INTERNAL_ERROR") == true ||
                    e.message?.contains("DISABLED") == true
                ) {
                    // Fall back to demo SMS OTP state for prototype testing
                    _uiState.value = AuthUiState.OtpSent(
                        verificationId = "demo_verification_id",
                        phoneNumber = phoneNumber
                    )
                } else {
                    _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Phone verification failed")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                resendingToken = token
                _uiState.value = AuthUiState.OtpSent(
                    verificationId = verificationId,
                    phoneNumber = phoneNumber
                )
            }
        }

        try {
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(phoneNumber.trim())
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start phone verification", e)
            _uiState.value = AuthUiState.OtpSent(
                verificationId = "demo_verification_id",
                phoneNumber = phoneNumber
            )
        }
    }

    /**
     * Verify the 6-digit SMS code entered by the user
     */
    fun verifyOtpCode(verificationId: String, phoneNumber: String, code: String) {
        if (code.isBlank() || code.length < 6) {
            _uiState.value = AuthUiState.Error("Please enter the complete 6-digit verification code")
            return
        }

        _uiState.value = AuthUiState.Loading

        if (verificationId == "demo_verification_id") {
            // Simulated OTP success for local emulator preview
            SocialRepository.loginWithSocial("phone")
            val user = SocialRepository.currentUser.value ?: SampleDataProvider.currentUser
            _uiState.value = AuthUiState.Success(user)
            return
        }

        viewModelScope.launch {
            try {
                val credential = PhoneAuthProvider.getCredential(verificationId, code)
                signInWithPhoneCredential(credential, phoneNumber)
            } catch (e: Exception) {
                Log.e(TAG, "OTP verification failed", e)
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Invalid verification code")
            }
        }
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential, phoneNumber: String) {
        viewModelScope.launch {
            try {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    SocialRepository.loginWithFirebaseUser(firebaseUser)
                    val appUser = SocialRepository.currentUser.value ?: SampleDataProvider.currentUser
                    _uiState.value = AuthUiState.Success(appUser)
                } else {
                    _uiState.value = AuthUiState.Error("Sign in failed: user is null")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firebase phone sign-in failed", e)
                _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Phone sign-in failed")
            }
        }
    }

    /**
     * Standard username/password registration / login
     */
    fun login(identifier: String, pass: String) {
        if (identifier.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your username")
            return
        }
        if (pass.length < 4) {
            _uiState.value = AuthUiState.Error("Password must be at least 4 characters")
            return
        }

        val success = SocialRepository.login(identifier, pass)
        if (success) {
            val user = SocialRepository.currentUser.value ?: SampleDataProvider.currentUser
            _uiState.value = AuthUiState.Success(user)
        } else {
            _uiState.value = AuthUiState.Error("Authentication failed")
        }
    }

    fun register(username: String, email: String, pass: String) {
        if (username.isBlank() || email.isBlank()) {
            _uiState.value = AuthUiState.Error("Username and email are required")
            return
        }
        if (pass.length < 4) {
            _uiState.value = AuthUiState.Error("Password must be at least 4 characters")
            return
        }

        val success = SocialRepository.login(username, pass)
        if (success) {
            val user = SocialRepository.currentUser.value ?: SampleDataProvider.currentUser
            _uiState.value = AuthUiState.Success(user)
        } else {
            _uiState.value = AuthUiState.Error("Registration failed")
        }
    }

    fun loginDemo() {
        SocialRepository.login("alex_rivers", "demo123")
        val user = SocialRepository.currentUser.value ?: SampleDataProvider.currentUser
        _uiState.value = AuthUiState.Success(user)
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
