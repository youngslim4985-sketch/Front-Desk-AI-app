package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.unit.sp
import com.example.R

// Google Fonts Provider Setup
val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Cormorant Garamond: Elegant Serif for Wordmarks / Display Headers
val CormorantGaramondFont = GoogleFont("Cormorant Garamond")
val CormorantGaramond = FontFamily(
    Font(googleFont = CormorantGaramondFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = CormorantGaramondFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = CormorantGaramondFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = CormorantGaramondFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

// Bebas Neue: High-Impact Condensed Sans-Serif for Numbers / Large Prices
val BebasNeueFont = GoogleFont("Bebas Neue")
val BebasNeue = FontFamily(
    Font(googleFont = BebasNeueFont, fontProvider = fontProvider, weight = FontWeight.Normal)
)

// Montserrat: Clean Modern Sans-Serif for Body text, Labels, and UI Components
val MontserratFont = GoogleFont("Montserrat")
val Montserrat = FontFamily(
    Font(googleFont = MontserratFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = MontserratFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = MontserratFont, fontProvider = fontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = MontserratFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

// Custom Typography Override conforming to Montserrat as default body text
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp
    ),
    displayMedium = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = CormorantGaramond,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
