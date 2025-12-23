package com.example.jbchretreatstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.jbchretreatstore.bookstore.data.testdata.TestDataLoader
import com.example.jbchretreatstore.bookstore.di.bookStoreModule
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavHost
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Composable
@Preview
fun App() {
    BookStoreTheme {
        KoinApplication(application = {
            modules(bookStoreModule)
        }) {
            // Load test data on app startup (comment out for production)
            LoadTestData()

            BookStoreNavHost()
        }
    }
}

/**
 * Composable that loads test data on app startup.
 * Comment out the call to this function in App() to disable test data loading.
 */
@Composable
private fun LoadTestData() {
    val testDataLoader: TestDataLoader = koinInject()

    LaunchedEffect(Unit) {
        // Reset flag to force reload (comment out this line after first run for one-time load)
//        testDataLoader.resetTestDataFlag()
        
        // Load test data if not already loaded
        testDataLoader.loadAllTestDataIfNeeded()

        // Option 2: Force load test data every time (uncomment if needed)
        // testDataLoader.loadAllTestData()
    }
}