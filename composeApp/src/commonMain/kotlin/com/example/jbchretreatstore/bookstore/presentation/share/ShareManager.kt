package com.example.jbchretreatstore.bookstore.presentation.share

/**
 * Platform-specific share manager for sharing CSV files
 */
interface ShareManager {
    /**
     * Share a CSV file with the given content
     * @param csvContent The CSV content to share
     * @param fileName The name of the file to share
     */
    suspend fun shareCsv(csvContent: String, fileName: String)
}

/**
 * Expect function to get platform-specific ShareManager instance
 */
expect fun getShareManager(): ShareManager

