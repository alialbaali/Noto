package com.noto.app.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.noto.app.NotoTheme

@Composable
fun NotoFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(NotoTheme.dimensions.medium),
        colors = colors,
    ) {
        Text(text, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun NotoOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(NotoTheme.dimensions.medium),
        colors = colors,
    ) {
        Text(text, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun NotoTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        contentPadding = PaddingValues(NotoTheme.dimensions.medium),
        colors = colors,
    ) {
        Text(text, style = MaterialTheme.typography.titleSmall)
    }
}