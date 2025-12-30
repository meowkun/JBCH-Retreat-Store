package com.example.jbchretreatstore.bookstore.presentation.ui.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Dimensions
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.SearchContentColor
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.SearchPlaceholderColor
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.clear_search_description
import jbchretreatstore.composeapp.generated.resources.ic_search
import jbchretreatstore.composeapp.generated.resources.search_icon_description
import jbchretreatstore.composeapp.generated.resources.search_placeholder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        modifier = modifier.minimumInteractiveComponentSize(),
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold
        ),
        shape = RoundedCornerShape(percent = Dimensions.corner_radius_percent_l),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = SearchContentColor,
            unfocusedTextColor = SearchContentColor,
            focusedBorderColor = MediumBlue,
            unfocusedBorderColor = MediumBlue,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            cursorColor = SearchContentColor,
            focusedPlaceholderColor = SearchPlaceholderColor,
            unfocusedPlaceholderColor = SearchPlaceholderColor,
        ),
        placeholder = {
            Text(
                text = stringResource(Res.string.search_placeholder),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = SearchPlaceholderColor
            )
        },
        leadingIcon = {
            Image(
                modifier = Modifier.wrapContentSize(),
                painter = painterResource(Res.drawable.ic_search),
                contentDescription = stringResource(Res.string.search_icon_description),
                contentScale = ContentScale.Fit
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.clear_search_description),
                        tint = SearchContentColor
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun ItemSearchBarPreview() {
    BookStoreTheme {
        ItemSearchBar(
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}