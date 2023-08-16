package com.noto.app.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.Theme

val Theme.colorScheme: ColorScheme
    @Composable
    get() {
        val isDarkMode = isSystemInDarkTheme()
        return when (this) {
            Theme.System -> if (isDarkMode) darkColorScheme else lightColorScheme
            Theme.SystemBlack -> if (isDarkMode) blackColorScheme else lightColorScheme
            Theme.Light -> lightColorScheme
            Theme.Dark -> darkColorScheme
            Theme.Black -> blackColorScheme
        }
    }

private val lightColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    secondary = Color(0xFF707070),
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF7F7F7),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF7F7F7),
    onSurfaceVariant = Color.Black,
    outline = Color(0xFF707070),
    errorContainer = Color(0xFFB00020),
    error = Color(0xFFB00020),
    onErrorContainer = Color.White,
    onError = Color.White,
)

private val darkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color(0xFFBDBDBD),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color.White,
    outline = Color(0xFFBDBDBD),
    errorContainer = Color(0xFFB00020),
    error = Color(0xFFB00020),
    onErrorContainer = Color.White,
    onError = Color.White,
)

private val blackColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = Color(0xFFBDBDBD),
    onSecondary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF121212),
    onSurfaceVariant = Color.White,
    outline = Color(0xFFBDBDBD),
    errorContainer = Color(0xFFB00020),
    error = Color(0xFFB00020),
    onErrorContainer = Color.White,
    onError = Color.White,
)

val ColorScheme.warning: Color
    get() = Color(0xFFFFA726)

@Composable
fun NotoColor.toColor() = when (this) {
    NotoColor.Gray -> if (isSystemInDarkTheme()) Color(0xFFBDBDBD) else Color(0xFF757575)
    NotoColor.Blue -> Color(0xFF42A5F5)
    NotoColor.Pink -> Color(0xFFEC407A)
    NotoColor.Cyan -> Color(0xFF26C6DA)
    NotoColor.Purple -> Color(0xFFAB47BC)
    NotoColor.Red -> Color(0xFFEF5350)
    NotoColor.Yellow -> Color(0xFFFFA726)
    NotoColor.Orange -> Color(0xFFD59E17)
    NotoColor.Green -> Color(0xFF66BB6A)
    NotoColor.Brown -> Color(0xFF8D6E63)
    NotoColor.BlueGray -> Color(0xFF78909C)
    NotoColor.Teal -> Color(0xFF26A69A)
    NotoColor.Indigo -> Color(0xFF5C6BC0)
    NotoColor.DeepPurple -> Color(0xFF7E57C2)
    NotoColor.DeepOrange -> Color(0xFFFF7043)
    NotoColor.DeepGreen -> Color(0xFF00C853)
    NotoColor.LightBlue -> Color(0xFF40C4FF)
    NotoColor.LightGreen -> Color(0xFF8BC34A)
    NotoColor.LightRed -> Color(0xFFFF8A80)
    NotoColor.LightPink -> Color(0xFFFF80AB)
    NotoColor.Black -> if (isSystemInDarkTheme()) Color.White else Color.Black
}