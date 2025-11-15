package com.example.jbchretreatstore

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.jbchretreatstore.bookstore.presentation.navigation.BookStoreNavHost
import com.example.jbchretreatstore.bookstore.presentation.BookStoreViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    BookStoreNavHost(
        viewModel = remember { BookStoreViewModel() }
    )
}