package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.SearchContentColor
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TitleView(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.spacing_m),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = SearchContentColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitleViewPreview() {
    BookStoreTheme {
        TitleView(title = "Sample Title")
    }
}
