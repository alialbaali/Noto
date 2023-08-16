package com.noto.app.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.BottomSheetDialogItem
import com.noto.app.theme.NotoTheme
import com.noto.app.util.Constants

class ReportIssueDialogFragment : BaseDialogFragment() {

    @Suppress("DEPRECATION")
    private val appVersion by lazy {
        context?.let { context ->
            context.packageManager?.getPackageInfo(context.packageName, 0)?.versionName
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                BottomSheetDialog(title = stringResource(id = R.string.report_issue)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.email),
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(Constants.EmailType)).apply {
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.Noto.Email))
                                    putExtra(Intent.EXTRA_SUBJECT, Constants.Noto.ReportIssueEmailSubject)
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        Constants.Noto.ReportIssueEmailBody(
                                            androidVersion = Build.VERSION.RELEASE.toString(),
                                            sdkVersion = Build.VERSION.SDK_INT.toString(),
                                            appVersion = appVersion.toString(),
                                        )
                                    )
                                }
                                startActivity(intent)

                            },
                            painter = painterResource(id = R.drawable.ic_round_email_24),
                        )

                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.github),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.GithubIssueUrl))
                                startActivity(intent)
                                dismiss()
                            },
                            painter = painterResource(id = R.drawable.ic_github_logo),
                        )
                    }
                }
            }
        }
    }
}