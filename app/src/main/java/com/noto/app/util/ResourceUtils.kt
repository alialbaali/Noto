package com.noto.app.util

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

fun Fragment.colorStateResource(@ColorRes id: Int): ColorStateList? = ResourcesCompat.getColorStateList(resources, id, null)
fun Fragment.colorResource(@ColorRes id: Int): Int = ResourcesCompat.getColor(resources, id, null)
fun Fragment.stringResource(@StringRes id: Int): String = getString(id)
fun Fragment.drawableResource(@DrawableRes id: Int): Drawable? = ResourcesCompat.getDrawable(resources, id, null)
fun Fragment.dimenResource(@DimenRes id: Int): Float = resources.getDimension(id)
fun Fragment.fontResource(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(requireContext(), id)

fun View.colorStateResource(@ColorRes id: Int): ColorStateList? = ResourcesCompat.getColorStateList(resources, id, null)
fun View.colorResource(@ColorRes id: Int): Int = ResourcesCompat.getColor(resources, id, null)
fun View.stringResource(@StringRes id: Int): String = context.getString(id)
fun View.drawableResource(@DrawableRes id: Int): Drawable? = ResourcesCompat.getDrawable(resources, id, null)
fun View.dimenResource(@DimenRes id: Int): Float = resources.getDimension(id)
fun View.fontResource(@FontRes id: Int): Typeface? = ResourcesCompat.getFont(context!!, id)