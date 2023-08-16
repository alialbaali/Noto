package com.noto.app.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.noto.app.R

private val initialTypography = Typography()

val typography
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