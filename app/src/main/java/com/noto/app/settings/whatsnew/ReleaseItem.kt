package com.noto.app.settings.whatsnew

import android.annotation.SuppressLint
import android.text.method.LinkMovementMethod
import android.view.View
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.noto.app.R
import com.noto.app.databinding.ReleaseItemBinding
import com.noto.app.domain.model.Release
import com.noto.app.util.format
import com.noto.app.util.removeLinksUnderline

@SuppressLint("NonConstantResourceId")
@EpoxyModelClass(layout = R.layout.release_item)
abstract class ReleaseItem : EpoxyModelWithHolder<ReleaseItem.Holder>() {

    @EpoxyAttribute
    lateinit var release: Release

    override fun bind(holder: Holder) = with(holder.binding) {
        tvVersion.text = release.versionFormatted
        tvDate.text = release.dateFormatted
        tvChangelog.text = release.changelogFormatted
        tvChangelog.removeLinksUnderline()
        tvChangelog.movementMethod = LinkMovementMethod.getInstance()
    }

    class Holder : EpoxyHolder() {
        lateinit var binding: ReleaseItemBinding
            private set

        override fun bindView(itemView: View) {
            binding = ReleaseItemBinding.bind(itemView)
        }
    }
}