package com.denis.rickmorty.episodes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.denis.domain.model.Destination
import com.denis.domain.model.NoResultsFound
import com.denis.domain.model.UiState
import com.denis.domain.model.episodes.EpisodeModel
import com.denis.rickmorty.ui.AppTheme
import com.denis.rickmorty.ui.SaveGridScrollState
import com.denis.rickmorty.ui.components.SimpleSearchBar
import com.denis.rickmorty.ui.shouldPaginate
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.compose.viewmodel.koinViewModel
import rickmorty.composeapp.generated.resources.*

@Composable
fun EpisodesListScreen(
    viewModel: EpisodesListViewModel = koinViewModel(),
    navigate: (Destination) -> Unit = {},
    show: Boolean = false,
) {
    val data by viewModel.data.collectAsStateWithLifecycle()

    if (show) {
        LifecycleResumeEffect(Unit) {
            viewModel.loadEpisodes()
            onPauseOrDispose {}
        }
        Content(data) { event ->
            when (event) {
                is EpisodesListUiEvent.ToggleSeason -> viewModel.toggleSeason(event.season)
                is EpisodesListUiEvent.EpisodeDetail -> navigate(Destination.EpisodeDetail(event.id))
                is EpisodesListUiEvent.SearchEpisodes -> viewModel.loadEpisodes(event.name)
            }
        }
    }
}

@Composable
private fun Content(
    data: EpisodesListUiData = EpisodesListUiData(),
    event: (EpisodesListUiEvent) -> Unit = {}
) {
    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemIndex = data.scrollState.value)
    SaveGridScrollState(lazyGridState, data.scrollState)

    val episodesBySeason = data.episodesBySeason()
    val episodes = data.episodes()
    val searchName = rememberTextFieldState(data.searchQuery)
    val onSearchName = { query: String -> event(EpisodesListUiEvent.SearchEpisodes(name = query)) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = lazyGridState,
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(2) }) {
            Text(
                text = stringResource(Res.string.episodes),
                fontSize = 10.em,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }

        item(span = { GridItemSpan(2) }) {
            SimpleSearchBar(
                stringResource(Res.string.search),
                searchName,
                onSearchName,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (data.searchQuery.isNotEmpty()) {
            items(episodes.size) { index ->
                EpisodeItem(episodes[index]) {
                    event(EpisodesListUiEvent.EpisodeDetail(episodes[index].id))
                }
            }
        } else {
            episodesBySeason.forEach { (season, seasonEpisodes) ->
                val isExpanded = data.expandedSeasons.contains(season)

                item(span = { GridItemSpan(2) }, key = season) {
                    Column {
                        SeasonHeader(
                            season = season,
                            isExpanded = isExpanded,
                            onToggle = { event(EpisodesListUiEvent.ToggleSeason(season)) }
                        )
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                seasonEpisodes.chunked(2).forEach { rowEpisodes ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowEpisodes.forEach { episode ->
                                            Box(modifier = Modifier.weight(1f)) {
                                                EpisodeItem(episode) {
                                                    event(EpisodesListUiEvent.EpisodeDetail(episode.id))
                                                }
                                            }
                                        }
                                        if (rowEpisodes.size == 1) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
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
        if (data.state == UiState.Loading) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun SeasonHeader(
    season: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.season_with_number, season.removePrefix("S").trimStart('0')),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) stringResource(Res.string.collapse) else stringResource(Res.string.expand),
                modifier = Modifier.rotate(rotation)
            )
        }
    }
}

@Composable
private fun EpisodeItem(episode: EpisodeModel, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = episode.episode,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = episode.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                minLines = 2,
                lineHeight = 20.sp
            )
            Text(
                text = episode.airDate,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun EpisodesContentPreview() {
    val _expandedSeasons = MutableStateFlow(listOf<String>("S01"))
    val expandedSeasons = _expandedSeasons.asStateFlow().collectAsState()
    val onEvent = { event: EpisodesListUiEvent ->
        when(event) {
            is EpisodesListUiEvent.ToggleSeason -> {
                if (_expandedSeasons.value.contains(event.season))
                    _expandedSeasons.value -= event.season
                else
                    _expandedSeasons.value += event.season
            }
            is EpisodesListUiEvent.EpisodeDetail -> {}
            is EpisodesListUiEvent.SearchEpisodes -> {}
        }
        Unit
    }
    AppTheme {
        Content(
            event = onEvent,
            data = EpisodesListUiData(
                state = UiState.Content,
                episodesMap = mapOf(
                    1 to listOf(
                        EpisodeModel(name = "Pilot", episode = "S01E01", airDate = "Dec 2, 2013"),
                        EpisodeModel(
                            name = "Lawnmower Dog",
                            episode = "S01E02",
                            airDate = "Dec 9, 2013"
                        )
                    ),
                    2 to listOf(
                        EpisodeModel(name = "Pilot", episode = "S02E01", airDate = "Dec 12, 2014"),
                        EpisodeModel(
                            name = "Lawnmower Dog",
                            episode = "S02E02",
                            airDate = "Dec 15, 2014"
                        )
                    )
                ),
                expandedSeasons = expandedSeasons.value.toSet()
            )
        )
    }
}
