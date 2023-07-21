package com.noto.app.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.util.Constants

private val TelegramColor = Color(0xFF2BACEF)
private val TelegramIconSize = 36.dp
private val TelegramTextSize = 18.sp

@Composable
fun Fragment.TelegramBanner(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(TelegramColor, MaterialTheme.shapes.small)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.TelegramUrl))
                startActivity(intent)
            }
            .padding(NotoTheme.dimensions.medium),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_telegram_logo_inverse),
            contentDescription = stringResource(id = R.string.telegram_community_description),
            modifier = Modifier.size(TelegramIconSize),
        )

        Spacer(modifier = Modifier.width(NotoTheme.dimensions.medium))

        Text(
            text = stringResource(id = R.string.join_telegram),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color.White,
            fontSize = TelegramTextSize,
            textAlign = TextAlign.Center,
        )
    }
}