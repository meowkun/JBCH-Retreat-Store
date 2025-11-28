package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions

@Composable
fun TitleView(title: String) {
    Text(
        modifier = Modifier.padding(top = Dimensions.spacing_m),
        text = title,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineSmall
    )

    HorizontalDivider(
        modifier = Modifier.padding(top = Dimensions.spacing_m)
    )
}