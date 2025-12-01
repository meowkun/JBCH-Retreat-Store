package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BottomGradientOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimensions.gradient_overlay_height)
            .background(
                Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    1.0f to Color.White
                )
            )
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0x000000
)
@Composable
fun BottomGradientOverlayPreview() {
    BookStoreTheme {
        BottomGradientOverlay()
    }
}

