package com.denis.rickmorty.characterdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.denis.domain.model.Destination
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.Status
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.rickmorty.ui.AppTheme
import com.denis.rickmorty.ui.components.Placeholder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.tooling.preview.Preview
import com.denis.rickmorty.toStringResource
import com.denis.rickmorty.ui.statusColor
import org.jetbrains.compose.resources.stringResource
import rickmorty.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CharacterDetailScreen(
    navigate: (Destination) -> Unit = {},
    id: Int = -1,
    viewModel: CharacterDetailViewModel = koinViewModel(),
) {
    LaunchedEffect(id) {
        viewModel.character(id)
    }

    val charactersData by viewModel.data.collectAsStateWithLifecycle()

    val onEvent = { event: CharacterDetailUiEvent ->
        when (event) {
            CharacterDetailUiEvent.Reload -> viewModel.character(id)
            is CharacterDetailUiEvent.OpenEpisode -> navigate(Destination.EpisodeDetail(event.id))
        }
    }

    Content(charactersData, onEvent)
}

@Composable
private fun Content(
    data: CharactersDetailUiData = CharactersDetailUiData(),
    event: (CharacterDetailUiEvent) -> Unit = {}
) {
    val character = data.character
    val episodesBySeason = remember(data.episodes) {
        data.episodes.groupBy { it.episode.substringBefore('E') }
    }
    val expandedSeasons = remember { mutableStateMapOf<String, Boolean>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val (imageState, onImageStateState) = remember {
                    mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty)
                }

                if (imageState !is AsyncImagePainter.State.Success) {
                    Placeholder(380.dp)
                }

                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(character.image)
                        .crossfade(true)
                        .diskCacheKey(character.image)
                        .build(),
                    contentDescription = character.name,
                    contentScale = ContentScale.FillWidth,
                    onState = onImageStateState
                )
            }
        }

        item {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = character.name.ifEmpty { stringResource(Res.string.name) },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                DetailRow(stringResource(Res.string.status), stringResource(character.status.toStringResource()), statusColor(character.status))
                DetailRow(stringResource(Res.string.species), character.species)
                DetailRow(stringResource(Res.string.gender), stringResource(character.gender.toStringResource()))
                DetailRow(stringResource(Res.string.location_label), character.location.name)
                DetailRow(stringResource(Res.string.origin), character.origin.name)
            }
        }

        if (data.episodes.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(Res.string.episodes_count, data.character.name, data.episodes.size),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            episodesBySeason.forEach { (season, episodes) ->
                val isExpanded = expandedSeasons[season] ?: false
                item {
                    val seasonNumber = season.substring(1).trimStart('0')
                    val seasonName = stringResource(Res.string.season_with_number, seasonNumber)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedSeasons[season] = !isExpanded }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = seasonName,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) stringResource(Res.string.collapse) else stringResource(Res.string.expand),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (isExpanded) {
                    items(episodes) { episode ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            EpisodeItem(episode) {
                                event(CharacterDetailUiEvent.OpenEpisode(episode.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: EpisodeModel, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = episode.episode,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
        HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
    }
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = valueColor,
                fontSize = 18.sp
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ContentPreview() {
    AppTheme {
        Content(data = CharactersDetailUiData(state = UiState.Content))
    }
}