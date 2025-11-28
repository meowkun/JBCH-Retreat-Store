package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.stepper_decrease
import jbchretreatstore.composeapp.generated.resources.stepper_increase
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Stepper(
    value: Int,
    onDecrement: (Int) -> Unit = {},
    onIncrement: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    minValue: Int = 1,
    maxValue: Int = Int.MAX_VALUE
) {
    Row(
        modifier = modifier
            .border(
                width = Dimensions.border_width,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(Dimensions.corner_radius_l)
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Minus button
        IconButton(
            onClick = {
                if (value > minValue) {
                    onDecrement(value - 1)
                }
            },
            enabled = value > minValue,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(Res.string.stepper_decrease)
            )
        }

        // Number display
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = Dimensions.spacing_s)
        )

        // Plus button
        IconButton(
            onClick = {
                if (value < maxValue) {
                    onIncrement(value + 1)
                }
            },
            enabled = value < maxValue,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.stepper_increase)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StepperPreview() {
    var previewValue by remember { mutableStateOf(2) }
    BookStoreTheme {
        Stepper(
            value = previewValue,
            onDecrement = { previewValue = it },
            onIncrement = { previewValue = it }
        )
    }
}
