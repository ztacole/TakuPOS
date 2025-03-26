package com.takumi.takupos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Navy,
    secondary = LightGray,
    onSecondary = Peach,
    onPrimary = White,
    background = White,
    onBackground = Gray,
    surface = White,
    onSurface = Navy,
    surfaceVariant = White,
    onSurfaceVariant = Green,
    primaryContainer = Orange,
    onPrimaryContainer = White,
    error = RedPastel,
    onError = White
)

@Composable
fun TakuPOSTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}