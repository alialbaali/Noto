package com.noto.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noto.app.NotoTheme
import com.noto.app.dialog
import com.noto.app.settings.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private val TipHeight = 5.dp
private val TipWidth = 30.dp

@Composable
fun BaseDialogFragment.BottomSheetDialog(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val viewModel by viewModel<SettingsViewModel>()
    val theme by viewModel.theme.collectAsState()
    NotoTheme(theme = theme) {
        Surface(
            shape = MaterialTheme.shapes.dialog,
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .padding(NotoTheme.dimensions.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Tip()
                Title(title)
                content()
            }
        }
    }
}

@Composable
private fun ColumnScope.Tip(modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(bottom = NotoTheme.dimensions.extraSmall)
            .size(TipWidth, TipHeight)
            .align(Alignment.CenterHorizontally)
            .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraLarge)
    )
}

@Composable
private fun Title(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(top = NotoTheme.dimensions.extraSmall, bottom = NotoTheme.dimensions.medium),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}