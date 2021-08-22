package com.noto.app.util

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat

fun Resources.colorStateResource(@ColorRes id: Int): ColorStateList? = ResourcesCompat.getColorStateList(this, id, null)
fun Resources.colorResource(@ColorRes id: Int): Int = ResourcesCompat.getColor(this, id, null)
fun Resources.stringResource(@StringRes id: Int): String = getString(id)
fun Resources.drawableResource(@DrawableRes id: Int): Drawable? = ResourcesCompat.getDrawable(this, id, null)
fun Resources.dimenResource(@DimenRes id: Int): Float = getDimension(id)
fun Resources.fontResource(context: Context, @FontRes id: Int) = ResourcesCompat.getFont(context, id)