package com.denis.rickmorty.episodedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.denis.domain.model.Destination
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.rickmorty.ui.AppTheme
import com.denis.rickmorty.ui.components.Placeholder
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import rickmorty.composeapp.generated.resources.Res
import rickmorty.composeapp.generated.resources.air_date
import rickmorty.composeapp.generated.resources.characters
import rickmorty.composeapp.generated.resources.created
import rickmorty.composeapp.generated.resources.episode_features
import rickmorty.composeapp.generated.resources.error_loading_episode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDetailScreen(
    episodeId: Int,
    navigate: (Destination) -> Unit = {},
    viewModel: EpisodeDetailViewModel = koinViewModel(),
    onBack: () -> Unit = {}
) {
    val data by viewModel.data.collectAsStateWithLifecycle()

    LifecycleResumeEffect(episodeId) {
        viewModel.loadEpisode(episodeId)
        onPauseOrDispose {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (data.state) {
            is UiState.Error -> Text(stringResource(Res.string.error_loading_episode), modifier = Modifier.align(Alignment.Center))
            is UiState.Loading, UiState.Content -> EpisodeDetailContent(
                episode = data.episode,
                characters = data.characters,
                onCharacterClick = { navigate(Destination.CharacterDetail(it)) }
            )
        }
    }
}

@Composable
private fun EpisodeDetailContent(
    state: UiState = UiState.Content,
    episode: EpisodeModel,
    characters: List<CharacterModel>,
    onCharacterClick: (Int) -> Unit
) {
    val gridColumnSize = 4
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridColumnSize),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item(span = { GridItemSpan(gridColumnSize) }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = episode.episode,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = episode.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item(span = { GridItemSpan(gridColumnSize) }) {
            InfoRow(label = stringResource(Res.string.air_date), value = episode.airDate)
        }

        item(span = { GridItemSpan(gridColumnSize) }) {
            InfoRow(label = stringResource(Res.string.created), value = episode.created)
        }

        item(span = { GridItemSpan(gridColumnSize) }) {
            Text(
                text = stringResource(Res.string.characters),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item(span = { GridItemSpan(gridColumnSize) }) {
            Text(
                text = stringResource(Res.string.episode_features, episode.characters.size),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (state is UiState.Content)
            items(characters) { character ->
                CharacterSmallItem(character = character, onClick = { onCharacterClick(character.id) })
            }
        if (state is UiState.Loading)
            items(listOf(
                CharacterModel(name = "Loading..."),
                CharacterModel(name = "Loading..."),
                CharacterModel(name = "Loading..."),
                CharacterModel(name = "Loading..."),
            )
            ) { character ->
                CharacterSmallItem(character = character, onClick = { onCharacterClick(character.id) })
            }
    }
}

@Composable
private fun CharacterSmallItem(character: CharacterModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        val (imageState, onImageStateState) = remember {
            mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
        }

        if (imageState !is AsyncImagePainter.State.Success) {
            Placeholder(80.dp)
            Text(
                text = character.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(character.image)
                .crossfade(true)
                .diskCacheKey(character.image)
                .build(),
            contentDescription = character.name,
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop,
            onState = onImageStateState
        )
        if (imageState is AsyncImagePainter.State.Success)
            Text(
                text = character.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.Gray)
        Text(text = value)
    }
}

@Composable
@Preview(showBackground = true)
private fun EpisodeDetailPreview() {
    AppTheme {
        EpisodeDetailContent(
            episode = EpisodeModel(
                name = "Pilot",
                episode = "S01E01",
                airDate = "December 2, 2013",
                created = "2017-11-10T12:56:33.798Z",
                characters = listOf("1", "2")
            ),
            characters = listOf(
                CharacterModel(id = 1, name = "Rick Sanchez"),
                CharacterModel(id = 2, name = "Morty Smith")
            ),
            onCharacterClick = {}
        )
    }
}
