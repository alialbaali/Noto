package com.noto.app.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.navArgs
import com.noto.app.theme.NotoTheme
import com.noto.app.util.Constants
import com.noto.app.util.navController

class ConfirmationDialogFragment : BaseDialogFragment() {

    private val args by navArgs<ConfirmationDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                BottomSheetDialog(title = args.btnText) {
                    Surface(shape = MaterialTheme.shapes.small) {
                        Column(
                            modifier = Modifier.padding(NotoTheme.dimensions.medium),
                            verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium),
                        ) {
                            Text(
                                text = args.confirmation,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = args.description,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.extraLarge))

                    Button(
                        text = args.btnText,
                        onClick = {
                            navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.ClickListener, 0)
                            dismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
        }
    }
}