package com.noto.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.Theme

@Composable
fun NotoTheme(theme: Theme, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDimensions provides dimensions) {
        with(theme) {
            MaterialTheme(colorScheme, shapes, typography, content)
        }
    }
}

object NotoTheme {
    val dimensions: Dimensions
        @Composable
        get() = LocalDimensions.current
}

private val dimensions = Dimensions()

private val Theme.colorScheme: ColorScheme
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

val ColorScheme.warning: Color
    get() = Color(0xFFFFA726)

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
)

private val shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

val Shapes.dialog
    get() = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)

private val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.nunito_regular_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.nunito_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.nunito_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.nunito_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.nunito_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.nunito_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.nunito_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.nunito_black, FontWeight.Black, FontStyle.Normal)
)

private val initialTypography = Typography()

private val typography
    @Composable
    get() = Typography(
        initialTypography.displayLarge.copy(fontFamily = NunitoFontFamily),
        initialTypography.displayMedium.copy(fontFamily = NunitoFontFamily),
        initialTypography.displaySmall.copy(fontFamily = NunitoFontFamily),
        initialTypography.headlineLarge.copy(fontFamily = NunitoFontFamily),
        initialTypography.headlineMedium.copy(
            // Note Title TextField, Placeholder
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
        ),
        initialTypography.headlineSmall.copy(fontFamily = NunitoFontFamily),
        initialTypography.titleLarge.copy(
            // Toolbar,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
        ),
        initialTypography.titleMedium.copy(
            // Dialog Title, Widget Title, Button
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Bold, // Or SemiBold
            fontSize = 20.sp,
        ),
        initialTypography.titleSmall.copy(
            // Folder Title, Note Title, Label Title,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
        ),
        initialTypography.bodyLarge.copy(
            // Dialog Section, Dialog RadioButton Item, Icon Item, Slider Label, Settings Item
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        ),
        initialTypography.bodyMedium.copy(
            // Note Body TextField, Tab Item,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
        initialTypography.bodySmall.copy(
            // Folder Notes Count, Note Body, Settings Item Value, ClickableView,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        ),
        initialTypography.labelLarge.copy(
            // SubTitle in Toolbar, Dialog Item,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        ),
        initialTypography.labelMedium.copy(
            // Note Label, Note Reminder,
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        ),
        initialTypography.labelSmall.copy(
            // Note Date
            fontFamily = NunitoFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp
        ),
    )

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

@Immutable
data class Dimensions(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
)

val LocalDimensions = staticCompositionLocalOf { Dimensions() }