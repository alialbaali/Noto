package com.noto.app.util

import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

private const val FileFormat = ".json"

fun writeDataToZipFile(outputStream: OutputStream, data: Map<String, String>) {
    ZipOutputStream(outputStream).use { zipOutputStream ->
        data.forEach { (fileName, json) ->
            val zipEntry = ZipEntry(fileName + FileFormat)
            zipOutputStream.putNextEntry(zipEntry)
            zipOutputStream.write(json.toByteArray())
            zipOutputStream.closeEntry()
        }
    }
}

fun readDataFromZipFile(inputStream: InputStream): Map<String, String> {
    val data = mutableMapOf<String, String>()
    ZipInputStream(inputStream).use { zipInputStream ->
        do {
            val zipEntry = zipInputStream.nextEntry
            if (zipEntry != null) {
                val fileName = zipEntry.name.substringAfterLast('/').removeSuffix(FileFormat)
                val json = zipInputStream.reader().readText()
                data[fileName] = json
            }
            zipInputStream.closeEntry()
        } while (zipEntry != null)
    }
    return data
}