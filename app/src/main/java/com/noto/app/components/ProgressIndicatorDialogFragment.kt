package com.noto.app.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.navArgs
import com.noto.app.theme.NotoTheme

private val ProgressIndicatorSize = 50.dp
private val ProgressIndicatorStrokeWidth = 5.dp

class ProgressIndicatorDialogFragment : BaseDialogFragment() {

    private val args by navArgs<ProgressIndicatorDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        isCancelable = false
        ComposeView(context).apply {
            setContent {
                BottomSheetDialog(title = args.title) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(NotoTheme.dimensions.medium)
                                .size(ProgressIndicatorSize),
                            strokeWidth = ProgressIndicatorStrokeWidth,
                            strokeCap = StrokeCap.Round,
                        )
                    }
                }
            }
        }
    }
}