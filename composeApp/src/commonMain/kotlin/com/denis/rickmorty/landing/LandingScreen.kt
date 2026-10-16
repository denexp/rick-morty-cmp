package com.denis.rickmorty.landing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denis.domain.model.Destination
import com.denis.rickmorty.characters.CharactersListScreen
import com.denis.rickmorty.episodes.EpisodesListScreen
import com.denis.rickmorty.locations.LocationsListScreen
import com.denis.rickmorty.ui.components.BottomNavigationBarComponent
import com.denis.rickmorty.ui.components.BottomNavigationItem
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import rickmorty.composeapp.generated.resources.Res
import rickmorty.composeapp.generated.resources.characters
import rickmorty.composeapp.generated.resources.ep
import rickmorty.composeapp.generated.resources.episodes
import rickmorty.composeapp.generated.resources.list_un
import rickmorty.composeapp.generated.resources.location
import rickmorty.composeapp.generated.resources.locations

@Composable
fun LandingScreen(navigate: (Destination) -> Unit) {
    val charactersLabel = stringResource(Res.string.characters)
    val episodesLabel = stringResource(Res.string.episodes)
    val locationsLabel = stringResource(Res.string.locations)

    val navItems = remember(charactersLabel, episodesLabel, locationsLabel) {
        listOf(
            BottomNavigationItem(
                index = 0,
                image = Res.drawable.list_un,
                selectedImage = Res.drawable.list_un,
                label = charactersLabel
            ),
            BottomNavigationItem(
                index = 1,
                image = Res.drawable.ep,
                selectedImage = Res.drawable.ep,
                label = episodesLabel
            ),
            BottomNavigationItem(
                index = 2,
                image = Res.drawable.location,
                selectedImage = Res.drawable.location,
                label = locationsLabel
            )
        )
    }
    val pagerState = rememberPagerState { navItems.size }
    val coroutine = rememberCoroutineScope()

    val onClickBottomBar: (Int) -> Unit = { index: Int ->
        coroutine.launch { pagerState.scrollToPage(index) }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBarComponent(
                id = "BottomNavigationComponent",
                items = navItems,
                itemSelectedId = pagerState.currentPage,
                onItemClick = onClickBottomBar,
            )
        },
    ) { innerPadding ->
        HorizontalPager(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = innerPadding.calculateBottomPadding())
                .fillMaxSize(),
            state = pagerState,
            beyondViewportPageCount = 2
        ) { i ->
            when(i) {
                0 -> {
                    CharactersListScreen(
                        navigate = navigate,
                        show = i == 0,
                    )
                }
                1 -> {
                    EpisodesListScreen(
                        navigate = navigate,
                        show = i == 1,
                    )
                }
                2 -> {
                    LocationsListScreen(
                        navigate = navigate,
                        show = i == 2,
                    )
                }
            }
        }
    }
}
