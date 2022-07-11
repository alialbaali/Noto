package com.noto.app.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import com.noto.app.BaseDialogFragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.NotoFilledButton
import com.noto.app.components.NotoOutlinedButton
import com.noto.app.util.surface

class VerifyEmailDialogFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)

        ComposeView(context).apply {
            setContent {
                val openWithText = stringResource(id = R.string.open_with)

                BottomSheetDialog(title = stringResource(id = R.string.verify_email)) {
                    Text(
                        text = stringResource(id = R.string.verify_email_info),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.surface(),
                    )

                    Spacer(Modifier.height(NotoTheme.dimensions.extraLarge))

                    Row(horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
                        NotoFilledButton(
                            text = stringResource(id = R.string.open_inbox),
                            onClick = { launchEmailAppChooser(openWithText) },
                            modifier = Modifier.weight(1F),
                        )

                        NotoOutlinedButton(
                            text = stringResource(id = R.string.edit_email),
                            onClick = { dismiss() },
                            modifier = Modifier.weight(1F),
                        )
                    }
                }
            }
        }
    }

    private fun launchEmailAppChooser(title: String) {
        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL)
            .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        val chooser = Intent.createChooser(intent, title)
        startActivity(chooser)
    }

}