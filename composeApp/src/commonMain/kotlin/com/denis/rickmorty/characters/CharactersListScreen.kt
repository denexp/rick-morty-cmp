package com.denis.rickmorty.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.denis.domain.model.Destination
import com.denis.domain.model.Destination.CharacterDetail
import com.denis.domain.model.NoResultsFound
import com.denis.domain.model.UiState
import com.denis.rickmorty.toStringResource
import com.denis.rickmorty.ui.AppTheme
import com.denis.rickmorty.ui.SaveListScrollState
import com.denis.rickmorty.ui.components.Placeholder
import com.denis.rickmorty.ui.components.SimpleSearchBar
import com.denis.rickmorty.ui.shouldPaginate
import com.denis.rickmorty.ui.statusColor
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import rickmorty.composeapp.generated.resources.Res
import rickmorty.composeapp.generated.resources.characters
import rickmorty.composeapp.generated.resources.error
import rickmorty.composeapp.generated.resources.no_results
import rickmorty.composeapp.generated.resources.search
import rickmorty.composeapp.generated.resources.status_label

@Composable
fun CharactersListScreen(
    viewModel: CharactersListViewModel = koinViewModel(),
    navigate: (Destination) -> Unit = {},
    show: Boolean = false,
) {
    val charactersData by viewModel.data.collectAsStateWithLifecycle()
    val onEvent = { event: CharactersListUiEvent ->
        when (event) {
            is CharactersListUiEvent.CharacterDetail -> navigate(CharacterDetail(event.id))
            is CharactersListUiEvent.Reload -> {
                viewModel.searchCharacters(name = event.name)
            }
            is CharactersListUiEvent.SearchCharacters -> {
                val page = if (event.nextPage)
                    viewModel.data.value.page + 1
                else 1

                viewModel.searchCharacters(page, event.name)
            }
        }
    }


    if (show) {
        LifecycleResumeEffect(Unit) {
            viewModel.searchCharacters()

            onPauseOrDispose {}
        }
        Content(charactersData, onEvent)
    }
}

@Composable
private fun Content(data: CharactersListUiData = CharactersListUiData(), event: (CharactersListUiEvent) -> Unit = {}) {
    val lazyState = rememberLazyListState(initialFirstVisibleItemIndex = data.scrollState.value)
    val characters = data.characters()
    val shouldPaginate by shouldPaginate(lazyState, characters, data.charactersInfo.count)
    val searchName = rememberTextFieldState(data.searchQuery)
    val onSearchName = { query: String -> event(CharactersListUiEvent.SearchCharacters(name = query)) }

    SaveListScrollState(lazyState, data.scrollState, headers = 0)

    LaunchedEffect(shouldPaginate) {
        if (shouldPaginate) event(CharactersListUiEvent.SearchCharacters(nextPage = true, name = searchName.text.toString()))
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = lazyState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = stringResource(Res.string.characters),
                fontSize = 10.em,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }
        item {
            SimpleSearchBar(
                stringResource(Res.string.search),
                searchName,
                onSearchName,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(characters) {
            Row(modifier =
                Modifier
                    .clickable { event(CharactersListUiEvent.CharacterDetail(it.id)) }
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box {
                    val (imageState, onImageStateState) = remember {
                        mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
                    }
                    val size = 100.dp

                    if (imageState !is AsyncImagePainter.State.Success)
                        Placeholder(size)

                    AsyncImage(
                        modifier = Modifier.size(size).clip(MaterialTheme.shapes.medium),
                        model = ImageRequest.Builder(LocalPlatformContext.current)
                            .data(it.image)
                            .crossfade(true)
                            .diskCacheKey(it.image)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        onState = onImageStateState
                    )
                }
                Column {
                    Text(it.name, fontWeight = FontWeight.Bold, fontSize = 4.em, modifier = Modifier.padding(horizontal = 8.dp))
                    Text(it.location.name, fontSize = 3.em, modifier = Modifier.padding(horizontal = 8.dp))
                    Row {
                        Text(
                            text = stringResource(Res.string.status_label),
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 3.em
                            )
                        )
                        Text(
                            text = stringResource(it.status.toStringResource()),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium,
                                color = statusColor(it.status),
                                fontSize = 3.em
                            )
                        )
                    }
                }
            }
        }
        if (data.state is UiState.Error) {
            item {
                val message = when(data.state.value) {
                    is NoResultsFound -> stringResource(Res.string.no_results)
                    else -> stringResource(Res.string.error)
                }
                Text(message, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }

        if (data.state == UiState.Loading)
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
    }

}

@Composable
@Preview(showBackground = true)
private fun ContentPreview() {
    AppTheme {
        Content()
    }
}