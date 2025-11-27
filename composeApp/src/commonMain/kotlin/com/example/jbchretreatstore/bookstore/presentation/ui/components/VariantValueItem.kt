package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.corner_radius_percent_m
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.GrayBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.LightGrey
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.ic_close
import jbchretreatstore.composeapp.generated.resources.remove_variant_value_description
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VariantValueItem(
    value: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = {},
            modifier = Modifier.weight(1f),
            enabled = false,
            singleLine = true,
            shape = RoundedCornerShape(corner_radius_percent_m),
            colors = TextFieldDefaults.colors(
                disabledContainerColor = LightGrey,
                disabledTextColor = Color.Black
            )
        )

        Spacer(Modifier.width(Dimensions.spacing_s))

        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(Res.drawable.ic_close),
                contentDescription = stringResource(Res.string.remove_variant_value_description),
                tint = GrayBlue
            )
        }
    }
}

@Preview
@Composable
fun VariantValueItemPreview() {
    BookStoreTheme {
        VariantValueItem(
            value = "Large",
            onRemove = {}
        )
    }
}

@Preview
@Composable
fun VariantValueItemLongPreview() {
    BookStoreTheme {
        VariantValueItem(
            value = "Extra Large with very long text",
            onRemove = {}
        )
    }
}


