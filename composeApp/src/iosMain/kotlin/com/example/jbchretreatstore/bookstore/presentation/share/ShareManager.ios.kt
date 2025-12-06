package com.example.jbchretreatstore.bookstore.presentation.share

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataUsingEncoding
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IOSShareManager : ShareManager {
    override suspend fun shareCsv(csvContent: String, fileName: String) {
        withContext(Dispatchers.IO) {
            try {
                // Get the temporary directory
                val tempPath = NSTemporaryDirectory()
                val filePath = "$tempPath$fileName"
                val fileURL = NSURL.fileURLWithPath(filePath)

                // Convert string to NSData
                val nsString = NSString.create(string = csvContent)
                val data = nsString.dataUsingEncoding(NSUTF8StringEncoding)

                // Write data to file
                data?.writeToURL(fileURL, atomically = true)

                withContext(Dispatchers.Main) {
                    // Create activity view controller for sharing
                    val activityViewController = UIActivityViewController(
                        activityItems = listOf(fileURL),
                        applicationActivities = null
                    )

                    // Present the share sheet
                    val rootViewController =
                        UIApplication.sharedApplication.keyWindow?.rootViewController
                    rootViewController?.presentViewController(
                        activityViewController,
                        animated = true,
                        completion = null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

actual fun getShareManager(): ShareManager {
    return IOSShareManager()
}

