package com.example.jbchretreatstore.bookstore.presentation.ui.shared

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Custom OutlinedTextField with no horizontal padding inside BasicTextField.
 * Uses BasicTextField internally and wraps content naturally.
 */
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    contentPadding: PaddingValues = PaddingValues(Dimensions.spacing_m)
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .then(
                if (label != null) {
                    Modifier
                        .semantics(mergeDescendants = true) {}
                        .padding(top = Dimensions.spacing_s)
                } else {
                    Modifier
                }
            ),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        decorationBox = @Composable { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder = placeholder,
                label = label,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                prefix = prefix,
                suffix = suffix,
                supportingText = supportingText,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = contentPadding,
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = interactionSource,
                        colors = colors,
                        shape = shape,
                    )
                },
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomOutlinedTextFieldPreview() {
    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("5") }
    var text3 by remember { mutableStateOf("Sample text") }
    var text4 by remember { mutableStateOf("") }

    BookStoreTheme {
        Surface {
            Column(
                modifier = Modifier.padding(Dimensions.spacing_m),
                verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_m)
            ) {
                Text(
                    "Default (no horizontal padding):",
                    style = MaterialTheme.typography.labelLarge
                )
                CustomOutlinedTextField(
                    value = text1,
                    onValueChange = { text1 = it },
                    placeholder = { Text("Enter text") }
                )

                Text(
                    "Quantity field example:",
                    style = MaterialTheme.typography.labelLarge
                )
                CustomOutlinedTextField(
                    value = text2,
                    onValueChange = { text2 = it },
                    singleLine = true
                )

                Text(
                    "With label:",
                    style = MaterialTheme.typography.labelLarge
                )
                CustomOutlinedTextField(
                    value = text3,
                    onValueChange = { text3 = it },
                    label = { Text("Name") }
                )

                Text(
                    "Custom padding override:",
                    style = MaterialTheme.typography.labelLarge
                )
                CustomOutlinedTextField(
                    value = text4,
                    onValueChange = { text4 = it },
                    placeholder = { Text("With padding") },
                    contentPadding = PaddingValues(
                        horizontal = Dimensions.spacing_s,
                        vertical = Dimensions.spacing_xs
                    )
                )
            }
        }
    }
}

