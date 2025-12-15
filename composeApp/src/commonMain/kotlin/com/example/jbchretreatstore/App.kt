package com.example.jbchretreatstore

import androidx.compose.runtime.Composable
import com.example.jbchretreatstore.bookstore.di.bookStoreModule
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavHost
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
fun App() {
    BookStoreTheme {
        KoinApplication(application = {
            modules(bookStoreModule)
        }) {
            BookStoreNavHost()
        }
    }
}