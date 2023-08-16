package com.noto.app.components

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.HeaderItemBinding
import com.noto.app.domain.model.NotoColor
import com.noto.app.theme.NotoTheme
import com.noto.app.util.*

@Composable
fun HeaderItem(
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )

            DividerItem(
                modifier = Modifier.weight(1F),
                paddingValues = PaddingValues(horizontal = NotoTheme.dimensions.medium)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderItem(
    title: String,
    isContentVisible: Boolean,
    onToggleContentClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentDescription = stringResource(if (isContentVisible) R.string.hide else R.string.show)
    val rotationDegrees by animateFloatAsState(targetValue = if (isContentVisible) 180F else 0F)
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
        Surface(
            checked = isContentVisible,
            onCheckedChange = onToggleContentClick,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.background,
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                )

                DividerItem(
                    modifier = Modifier.weight(1F),
                    paddingValues = PaddingValues(horizontal = NotoTheme.dimensions.medium)
                )

                IconToggleButton(
                    checked = isContentVisible,
                    onCheckedChange = onToggleContentClick,
                    colors = IconButtonDefaults.iconToggleButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary,
                        checkedContentColor = MaterialTheme.colorScheme.secondary,
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_round_arrow_down_24),
                        contentDescription = contentDescription,
                        modifier = Modifier.rotate(rotationDegrees)
                    )
                }
            }
        }
    }
}

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass
abstract class HeaderItem : EpoxyModelWithHolder<HeaderItem.Holder>() {

    @EpoxyAttribute
    lateinit var title: String

    @EpoxyAttribute
    open var isVisible = false

    @EpoxyAttribute
    var color: NotoColor? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onClickListener: View.OnClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onLongClickListener: View.OnLongClickListener? = null

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onCreateClickListener: View.OnClickListener? = null

    override fun bind(holder: Holder) = with(holder.binding) {
        tvTitle.text = title
        root.setOnClickListener(onClickListener)
        root.setOnLongClickListener(onLongClickListener)
        root.isClickable = onClickListener != null
        root.isLongClickable = onLongClickListener != null
        ibVisibility.animate().setDuration(DefaultAnimationDuration).rotation(if (isVisible) 180F else 0F)
        ibVisibility.contentDescription = root.context?.stringResource(if (isVisible) R.string.hide else R.string.show)
        ibVisibility.setOnClickListener(onClickListener)
        ibVisibility.isVisible = onClickListener != null
        ibCreate.setOnClickListener(onCreateClickListener)
        ibCreate.isVisible = onCreateClickListener != null
        if (color != null) {
            val colorResource = root.context.colorResource(color!!.toColorResourceId())
            val colorStateList = colorResource.toColorStateList()
            tvTitle.setTextColor(colorResource)
            vDivider.background?.mutate()?.setTint(colorResource.withDefaultAlpha())
            ibVisibility.imageTintList = colorStateList
            ibCreate.imageTintList = colorStateList
            root.background.setRippleColor(colorStateList)
            ibCreate.background.setRippleColor(colorStateList)
            ibVisibility.background.setRippleColor(colorStateList)
        } else {
            val colorResource = root.context.colorAttributeResource(R.attr.notoSecondaryColor)
            tvTitle.setTextColor(colorResource)
            ibVisibility.imageTintList = colorResource.toColorStateList()
            ibCreate.imageTintList = colorResource.toColorStateList()
        }
    }

    override fun onViewAttachedToWindow(holder: Holder) {
        super.onViewAttachedToWindow(holder)
        holder.binding.root.setFullSpan()
    }

    override fun getDefaultLayout(): Int = R.layout.header_item

    class Holder : EpoxyHolder() {
        lateinit var binding: HeaderItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = HeaderItemBinding.bind(itemView)
        }
    }
}