package com.software.financetracker.core.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ExportShareHelper {

    fun save(context: Context, csvContent: String) {
        val filename = "financetracker_export_${System.currentTimeMillis()}.csv"
        val saved = saveToDownloads(context, csvContent, filename)
        Toast.makeText(
            context,
            if (saved) "Guardado en Descargas" else "Error al guardar el archivo",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun share(context: Context, csvContent: String, title: String = "Compartir CSV") {
        val filename = "financetracker_export_${System.currentTimeMillis()}.csv"
        val exportsDir = File(context.cacheDir, "exports")
        exportsDir.mkdirs()
        val file = File(exportsDir, filename)
        file.writeText(csvContent, Charsets.UTF_8)

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    private fun saveToDownloads(context: Context, csvContent: String, filename: String): Boolean {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return false
        return try {
            resolver.openOutputStream(uri)?.use { it.write(csvContent.toByteArray(Charsets.UTF_8)) }
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
            true
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            false
        }
    }
}
