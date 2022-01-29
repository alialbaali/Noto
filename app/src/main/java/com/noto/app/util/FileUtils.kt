package com.noto.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

suspend fun writeTextToOutputStream(outputStream: OutputStream, text: String) = withContext(Dispatchers.IO) {
    outputStream.use { outputStream ->
        outputStream.write(text.toByteArray())
    }
}

suspend fun readTextFromInputStream(inputStream: InputStream): String = withContext(Dispatchers.IO) {
    inputStream.reader().use { inputStreamReader ->
        inputStreamReader.readText()
    }
}