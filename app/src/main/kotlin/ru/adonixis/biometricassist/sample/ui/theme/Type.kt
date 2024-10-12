package ru.adonixis.biometricassist.sample.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ru.adonixis.biometricassistsample.R

val latoFamily = FontFamily(
    Font(R.font.lato_regular, FontWeight.Normal),
    Font(R.font.lato_bold, FontWeight.Bold),
    Font(R.font.lato_black, FontWeight.Black)
)

private val defaultTypography = Typography()

val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = latoFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = latoFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = latoFamily),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = latoFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = latoFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = latoFamily),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = latoFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = latoFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = latoFamily),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = latoFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = latoFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = latoFamily),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = latoFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = latoFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = latoFamily)
)
