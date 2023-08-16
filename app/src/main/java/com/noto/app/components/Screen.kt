package com.noto.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.noto.app.settings.SettingsViewModel
import com.noto.app.theme.NotoTheme
import com.noto.app.util.navController
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Fragment.Screen(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onNavigationIconClick: (() -> Unit)? = { navController?.navigateUp() },
    actions: @Composable RowScope.() -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(NotoTheme.dimensions.medium),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollState = rememberScrollState()
    val viewModel by viewModel<SettingsViewModel>()
    val theme by viewModel.theme.collectAsState()
    val scope = rememberCoroutineScope()
    val isScrolling by remember { derivedStateOf { scrollState.value > 0 } }
    NotoTheme(theme = theme) {
        Scaffold(
            topBar = {
                NotoTopAppbar(
                    title = title,
                    onClick = {
                        scope.launch {
                            scrollState.animateScrollTo(0)
                        }
                    },
                    isScrolling = isScrolling,
                    onNavigationIconClick = onNavigationIconClick,
                    actions = actions,
                    subtitle = subtitle,
                )
            },
            snackbarHost = snackbarHost
        ) { contentPadding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(NotoTheme.dimensions.medium)
                    .padding(contentPadding),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content,
            )
        }
    }
}