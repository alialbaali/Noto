package com.noto.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.noto.app.NotoTheme
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.navController
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fragment.Screen(
    title: String,
    modifier: Modifier = Modifier,
    onNavigationIconClick: (() -> Unit)? = { navController?.navigateUp() },
    snackbarHost: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val viewModel by viewModel<SettingsViewModel>()
    val theme by viewModel.theme.collectAsState()
    NotoTheme(theme = theme) {
        Scaffold(
            topBar = {
                NotoTopAppbar(
                    title = title,
                    onNavigationIconClick = onNavigationIconClick,
                )
            },
            snackbarHost = snackbarHost
        ) { contentPadding ->
            Column(
                modifier = modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement,
                horizontalAlignment,
                content = content,
            )
        }
    }
}