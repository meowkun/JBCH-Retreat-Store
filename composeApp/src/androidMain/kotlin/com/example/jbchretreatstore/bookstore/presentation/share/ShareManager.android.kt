package com.example.jbchretreatstore.bookstore.presentation.share

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class AndroidShareManager(private val context: Context) : ShareManager {
    override suspend fun shareCsv(csvContent: String, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                // Create a temporary file in the cache directory
                val cacheDir = context.cacheDir
                val file = File(cacheDir, fileName)
                file.writeText(csvContent)

                // Create a content URI using FileProvider
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                // Create share intent
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_SUBJECT, "Purchase History")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                val chooser = Intent.createChooser(shareIntent, "Share Purchase History")
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(chooser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

private var shareManagerInstance: ShareManager? = null

fun initShareManager(context: Context) {
    shareManagerInstance = AndroidShareManager(context.applicationContext)
}

actual fun getShareManager(): ShareManager {
    return shareManagerInstance
        ?: throw IllegalStateException("ShareManager not initialized. Call initShareManager() first.")
}

