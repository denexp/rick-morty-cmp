package com.denis.rickmorty.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import rickmorty.composeapp.generated.resources.Res
import rickmorty.composeapp.generated.resources.clear_search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    hint: String,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Controls expansion state of the search bar
    var expanded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(textFieldState.text) {
        snapshotFlow { textFieldState.text }
            .debounce(450)
            .distinctUntilChanged()
            .collect { onSearch(textFieldState.text.toString()) }
    }
    Box(
        modifier.background(color = MaterialTheme.colorScheme.surface, shape = CircleShape)
    ) {
        SearchBarDefaults.InputField(
            query = textFieldState.text.toString(),
            onQueryChange = {
                textFieldState.edit { replace(0, length, it) }
            },
            onSearch = {
                onSearch(textFieldState.text.toString())
                expanded = false
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            placeholder = { Text(hint) },
            trailingIcon = {
                if (textFieldState.text.isNotEmpty()) {
                    IconButton(onClick = {
                        textFieldState.edit { replace(0, length, "") }
                        onSearch("")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.clear_search)
                        )
                    }
                }
            }
        )
    }
}
