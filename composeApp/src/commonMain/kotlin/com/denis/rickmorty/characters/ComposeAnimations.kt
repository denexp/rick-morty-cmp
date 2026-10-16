package com.denis.rickmorty.characters

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment

object ComposeAnimations {
    private const val DURATION = 300
    @Stable
    fun enterTransition() = fadeIn(
        animationSpec = tween(DURATION, easing = LinearEasing)
    ) + expandHorizontally(
        animationSpec = tween(DURATION, easing = EaseIn),
    )
    @Stable
    fun exitTransition() = fadeOut(
        animationSpec = tween(DURATION, easing = LinearEasing)
    ) + shrinkHorizontally(
        animationSpec = tween(DURATION, easing = EaseOut),
        shrinkTowards = Alignment.End
    )

    //@Stable
    //fun enterTransition() = fadeIn(
    //    animationSpec = tween(DURATION, easing = LinearEasing)
    //) + slideInHorizontally(
    //    animationSpec = tween(DURATION, easing = EaseIn),
    //)
    //@Stable
    //fun exitTransition() = fadeOut(
    //    animationSpec = tween(DURATION, easing = LinearEasing)
    //) + slideOutHorizontally(
    //    animationSpec = tween(DURATION, easing = EaseOut),
    //)
}