package com.denis.rickmorty

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.denis.domain.model.Destination
import com.denis.rickmorty.characterdetail.CharacterDetailScreen
import com.denis.rickmorty.episodedetail.EpisodeDetailScreen
import com.denis.rickmorty.landing.LandingScreen
import com.denis.rickmorty.ui.AppTheme
import org.jetbrains.compose.resources.painterResource
import rickmorty.composeapp.generated.resources.Res
import rickmorty.composeapp.generated.resources.ic_arrow_left
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun App() {
    AppTheme {
        val navController = rememberNavController()
        val startDestination = Destination.LandingScreen
        val navigate = { destiny: Destination ->

            when(destiny) {
                else -> navController.navigate(destiny)
            }
        }
        var showTopBar by remember { mutableStateOf(false) }
        Scaffold(
            topBar = {
                if (showTopBar)
                    Row(Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .windowInsetsPadding(TopAppBarDefaults.windowInsets)) {
                        Icon(modifier = Modifier.size(30.dp).clickable { navController.navigateUp() }, painter = painterResource(Res.drawable.ic_arrow_left),
                            contentDescription = null)
                    }
            }
        ) { innerPadding ->
            NavHost(navController, startDestination, Modifier.padding(innerPadding)) {
                composable<Destination.LandingScreen>(popExitTransition = null, popEnterTransition = null) {
                    showTopBar = false
                    LandingScreen(navigate)
                }
                composable<Destination.CharacterDetail>(popExitTransition = null, popEnterTransition = null) {
                    showTopBar = true
                    val id = it.toRoute<Destination.CharacterDetail>().characterId
                    CharacterDetailScreen(navigate, id)
                }
                composable<Destination.EpisodeDetail>(popExitTransition = null, popEnterTransition = null) {
                    showTopBar = true
                    val id = it.toRoute<Destination.EpisodeDetail>().episodeId
                    EpisodeDetailScreen(id, navigate, onBack = { navController.navigateUp() })
                }
            }
        }
    }
}

public inline fun <reified T : Any> NavGraphBuilder.composableWithTopBar(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline enterTransition:
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
    EnterTransition?)? =
        null,
    noinline exitTransition:
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
    ExitTransition?)? =
        null,
    noinline popEnterTransition:
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
    EnterTransition?)? =
        enterTransition,
    noinline popExitTransition:
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
    ExitTransition?)? =
        exitTransition,
    noinline sizeTransform:
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
    SizeTransform?)? =
        null,
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        T::class,
        typeMap,
        deepLinks,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        sizeTransform,
        content
    )
}