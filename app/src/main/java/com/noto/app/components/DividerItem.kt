package com.noto.app.components

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.databinding.DividerItemBinding

private const val DefaultAlphaColor = 0.125F
private val DividerItemHeight = 3.dp

@Composable
fun DividerItem(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(NotoTheme.dimensions.medium),
    color: Color = MaterialTheme.colorScheme.surface,
    alpha: Float = if (color == MaterialTheme.colorScheme.surface) 1F else DefaultAlphaColor,
) {
    Box(
        modifier = modifier
            .padding(paddingValues)
            .height(DividerItemHeight)
            .background(color.copy(alpha = alpha), MaterialTheme.shapes.extraLarge)
    )
}

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class DividerItem : EpoxyModelWithHolder<DividerItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.divider_item

    class Holder : EpoxyHolder() {
        lateinit var binding: DividerItemBinding

        override fun bindView(itemView: View) {
            binding = DividerItemBinding.bind(itemView)
        }
    }
}