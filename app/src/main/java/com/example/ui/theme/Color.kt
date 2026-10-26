package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Instagram Signature Sunset Gradient Colors
val InstaPurple = Color(0xFF833AB4)
val InstaPink = Color(0xFFC13584)
val InstaRed = Color(0xFFE1306C)
val InstaOrange = Color(0xFFFD1D1D)
val InstaYellow = Color(0xFFFCAF45)

val InstagramGradient = Brush.linearGradient(
    colors = listOf(InstaPurple, InstaPink, InstaRed, InstaOrange, InstaYellow)
)

val StoryGradient = Brush.sweepGradient(
    colors = listOf(InstaYellow, InstaOrange, InstaRed, InstaPink, InstaPurple, InstaYellow)
)

// UI Theme Palette
val LikeRed = Color(0xFFED4956)
val VerifiedBlue = Color(0xFF3897F0)
val DarkBackground = Color(0xFF0F0F12)
val DarkSurface = Color(0xFF1A1A20)
val DarkBorder = Color(0xFF282832)

val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFFAFAFA)
val LightBorder = Color(0xFFE8E8EE)

val TextPrimaryDark = Color(0xFFF5F5F7)
val TextSecondaryDark = Color(0xFFA0A0AB)

val TextPrimaryLight = Color(0xFF1E1E24)
val TextSecondaryLight = Color(0xFF757582)
