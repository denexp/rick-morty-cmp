package com.denis.rickmorty.locations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.denis.domain.model.Destination
import com.denis.domain.model.NoResultsFound
import com.denis.domain.model.UiState
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.locations.LocationModel
import com.denis.rickmorty.ui.AppTheme
import com.denis.rickmorty.ui.SaveGridScrollState
import com.denis.rickmorty.ui.components.SimpleSearchBar
import com.denis.rickmorty.ui.shouldPaginate
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import rickmorty.composeapp.generated.resources.*

@Composable
fun LocationsListScreen(
    viewModel: LocationsListViewModel = koinViewModel(),
    navigate: (Destination) -> Unit = {},
    show: Boolean = false,
) {
    val data by viewModel.data.collectAsStateWithLifecycle()

    if (show) {
        LifecycleResumeEffect(Unit) {
            viewModel.loadLocations()
            onPauseOrDispose {}
        }
        Content(data) { event ->
            when (event) {
                is LocationsListUiEvent.LoadMore -> viewModel.loadLocations(event.page, event.name)
                is LocationsListUiEvent.ToggleLocation -> viewModel.toggleLocation(event.locationId)
                is LocationsListUiEvent.CharacterDetail -> navigate(Destination.CharacterDetail(event.id))
                is LocationsListUiEvent.SearchLocations -> {
                    val page = if (event.nextPage) data.page + 1 else 1
                    viewModel.loadLocations(page, event.name)
                }
            }
        }
    }
}

@Composable
private fun Content(
    data: LocationsListUiData = LocationsListUiData(),
    event: (LocationsListUiEvent) -> Unit = {}
) {
    val lazyGridState = rememberLazyGridState(initialFirstVisibleItemIndex = data.scrollState.value)
    SaveGridScrollState(lazyGridState, data.scrollState)

    val shouldPaginate by shouldPaginate(lazyGridState, data.locations, data.locationsInfo.count)
    val searchName = rememberTextFieldState(data.searchQuery)
    val onSearchName = { query: String -> event(LocationsListUiEvent.SearchLocations(name = query)) }

    LaunchedEffect(shouldPaginate) {
        if (shouldPaginate && !data.loadingMore) {
            event(LocationsListUiEvent.LoadMore(data.page + 1, searchName.text.toString()))
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        state = lazyGridState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(Res.string.locations),
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

        items(data.locations, key = { it.id }) { location ->
            val isExpanded = data.expandedLocations.contains(location.id)
            val residents = data.residents[location.id] ?: emptyList()

            LocationCard(
                location = location,
                isExpanded = isExpanded,
                residents = residents,
                onToggle = { event(LocationsListUiEvent.ToggleLocation(location.id)) },
                onCharacterClick = { event(LocationsListUiEvent.CharacterDetail(it)) }
            )
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
        if (data.state == UiState.Loading || data.loadingMore) {
            item {
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
private fun LocationCard(
    location: LocationModel,
    isExpanded: Boolean,
    residents: List<CharacterModel>,
    onToggle: () -> Unit,
    onCharacterClick: (Int) -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
    val hasResidents = location.residents.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (hasResidents) Modifier.clickable { onToggle() } else Modifier),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = location.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    val type = location.type.replace("unknown", "")
                    val dimension = location.dimension.replace("unknown", stringResource(Res.string.dimension_unknown))
                    val subName = if (type.isNotEmpty() && dimension.isNotEmpty())
                        type + stringResource(Res.string.location_separator) + dimension
                    else
                        type.ifEmpty { dimension }

                    Text(
                        text = subName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!isExpanded) {
                        Text(
                            text = stringResource(Res.string.residents_count, location.residents.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (hasResidents) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                if (hasResidents) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) stringResource(Res.string.collapse) else stringResource(Res.string.expand),
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                label = "ResidentsExpansion",
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = stringResource(Res.string.residents_with_count, location.residents.size),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    if (location.residents.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.no_residents),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else if (residents.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    } else {
                        ResidentsGrid(residents, onCharacterClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResidentsGrid(
    residents: List<CharacterModel>,
    onCharacterClick: (Int) -> Unit
) {
    // 1. Ensure the list is stable for the layout
    val residentList = remember(residents) { residents }

    // 2. Use FlowRow to avoid manual chunking and nested Row/Column overhead
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4 // Mimics your chunked(4) logic
    ) {
        residentList.forEach { character ->
            ResidentItem(
                character = character,
                // Use a fixed width or fraction to ensure 4 items fit properly
                modifier = Modifier.fillMaxWidth(0.22f),
                onClick = { onCharacterClick(character.id) }
            )
        }
    }
}

@Composable
private fun ResidentItem(
    character: CharacterModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalPlatformContext.current)
                .data(character.image)
                .crossfade(true)
                .diskCacheKey(character.image)
                .build(),
            contentDescription = character.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = character.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LocationsListPreview() {
    AppTheme {
        Content(
            data = LocationsListUiData(
                state = UiState.Content,
                locations = listOf(
                    LocationModel(
                        id = 1,
                        name = "Earth (C-137)",
                        type = "Planet",
                        dimension = "Dimension C-137",
                        residents = listOf("1", "2")
                    )
                ),
                expandedLocations = setOf(1),
                residents = mapOf(
                    1 to listOf(
                        CharacterModel(id = 1, name = "Rick Sanchez", image = ""),
                        CharacterModel(id = 2, name = "Morty Smith", image = "")
                    )
                )
            )
        )
    }
}
