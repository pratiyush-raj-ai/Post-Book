package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme

import androidx.lifecycle.lifecycleScope
import com.example.data.FirebaseAuthHelper
import com.example.data.SocialRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firestore repository with named database ID
        SocialRepository.initFirestore(this)

        // Attempt silent auto-sign-in with Google Credential Manager
        FirebaseAuthHelper.attemptAutoSignIn(
            context = this,
            scope = lifecycleScope,
            onSuccess = {},
            onUnauthenticated = {}
        )

        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemInDark) }

            MyApplicationTheme(darkTheme = isDarkMode) {
                MainScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { isDarkMode = !isDarkMode }
                )
            }
        }
    }
}
