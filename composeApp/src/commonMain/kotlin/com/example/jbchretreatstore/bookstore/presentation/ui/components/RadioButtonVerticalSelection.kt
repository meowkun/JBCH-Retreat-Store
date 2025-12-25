package com.example.jbchretreatstore.bookstore.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.example.jbchretreatstore.bookstore.domain.model.PaymentMethod
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions

@Composable
fun RadioButtonVerticalSelection(
    radioOptions: List<PaymentMethod>,
    selectedOption: PaymentMethod,
    onOptionSelected: (PaymentMethod) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.spacing_xs)
    ) {
        radioOptions.forEach { paymentMethod ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = paymentMethod == selectedOption,
                        onClick = { onOptionSelected(paymentMethod) },
                        role = Role.RadioButton
                    )
                    .padding(Dimensions.spacing_xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = paymentMethod == selectedOption,
                    onClick = { onOptionSelected(paymentMethod) }
                )
                Text(
                    text = paymentMethod.methodName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

