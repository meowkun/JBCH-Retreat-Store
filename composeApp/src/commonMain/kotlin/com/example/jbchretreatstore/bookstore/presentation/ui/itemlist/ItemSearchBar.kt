package com.example.jbchretreatstore.bookstore.presentation.ui.itemlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.jbchretreatstore.bookstore.presentation.BookStoreIntent
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.Alpha
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BookStoreTheme
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.BrightBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.LightBlue
import com.example.jbchretreatstore.bookstore.presentation.ui.theme.MediumBlue
import jbchretreatstore.composeapp.generated.resources.Res
import jbchretreatstore.composeapp.generated.resources.clear_hint
import jbchretreatstore.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ItemSearchBar(
    searchQuery: String,
    onUserIntent: (BookStoreIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = MediumBlue,
            backgroundColor = MediumBlue.copy(alpha = Alpha.search_bar_background)
        )
    ) {
        OutlinedTextField(
            modifier = modifier.background(
                shape = RoundedCornerShape(percent = 100), color = LightBlue
            ).minimumInteractiveComponentSize(),
            value = searchQuery,
            onValueChange = { query ->
                onUserIntent(BookStoreIntent.OnSearchQueryChange(query))
            },
            shape = RoundedCornerShape(percent = 100),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = BrightBlue,
                focusedBorderColor = MediumBlue,
            ),
            placeholder = {
                Text(text = stringResource(Res.string.search_hint))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.search_hint),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = Alpha.search_bar_icon)
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = searchQuery.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            onUserIntent(BookStoreIntent.OnSearchQueryChange(""))
                        }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.clear_hint),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            })
    }
}


@Preview
@Composable
fun ItemSearchBarPreview() {
    BookStoreTheme {
        ItemSearchBar(
            searchQuery = "Bible",
            onUserIntent = {}
        )
    }
}