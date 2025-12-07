package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions.corner_radius_percent_m
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.LabelGray
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.LightGrey
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.White
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isError: Boolean = false,
    errorMessage: String = "",
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Text(
        modifier = Modifier.padding(start = Dimensions.spacing_m),
        text = label,
        style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold
        ),
        color = LabelGray
    )

    Spacer(Modifier.height(Dimensions.spacing_xs))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        isError = isError,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        placeholder = if (placeholder.isNotEmpty()) {
            {
                Text(
                    text = placeholder,
                    color = LabelGray,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        } else null,
        supportingText = if (isError && errorMessage.isNotEmpty()) {
            {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else null,
        shape = RoundedCornerShape(corner_radius_percent_m),
        keyboardOptions = keyboardOptions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = White,
            unfocusedContainerColor = White,
            focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            disabledContainerColor = LightGrey,
            disabledTextColor = Color.Black
        )
    )
}

@Preview
@Composable
fun LabeledTextFieldEmptyPreview() {
    BookStoreTheme {
        LabeledTextField(
            value = "",
            onValueChange = {},
            label = "Price",
            placeholder = "e.g. 37.99"
        )
    }
}

@Preview
@Composable
fun LabeledTextFieldErrorPreview() {
    BookStoreTheme {
        LabeledTextField(
            value = "Size",
            onValueChange = {},
            label = "Variant Key",
            placeholder = "e.g. Size, Color, Language",
            isError = true,
            errorMessage = "Variant with this name already exists"
        )
    }
}

