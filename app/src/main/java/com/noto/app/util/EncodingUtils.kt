package com.noto.app.util

import android.util.Base64

private const val Flags = Base64.NO_PADDING

//On Desktop, use java.util.Base64 (withoutPadding) by using expect /actual mechanism.
fun ByteArray.encodeToString(): String = Base64.encodeToString(this, Flags)

fun String.decodeToByteArray(): ByteArray = Base64.decode(this, Flags)