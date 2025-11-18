package com.example.jbchretreatstore

import androidx.compose.runtime.Composable
import com.example.jbchretreatstore.bookstore.di.bookStoreModule
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewModel
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavHost
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    BookStoreTheme {
        KoinApplication(application = {
            modules(bookStoreModule)
        }) {
            val viewModel = koinViewModel<BookStoreViewModel>()
            BookStoreNavHost(viewModel = viewModel)
        }
    }
}