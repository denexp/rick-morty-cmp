package com.denis.rickmorty

import com.denis.domain.model.characters.Gender
import com.denis.domain.model.characters.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import rickmorty.composeapp.generated.resources.Res
import rickmorty.composeapp.generated.resources.gender_female
import rickmorty.composeapp.generated.resources.gender_genderless
import rickmorty.composeapp.generated.resources.gender_male
import rickmorty.composeapp.generated.resources.gender_unknown
import rickmorty.composeapp.generated.resources.status_alive
import rickmorty.composeapp.generated.resources.status_dead
import rickmorty.composeapp.generated.resources.status_unknown
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchCatching(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    return launch(context, start) {
        runCatching { block() }
    }
}

fun Status.toStringResource(): StringResource {
    return when (this) {
        Status.Alive -> Res.string.status_alive
        Status.Dead -> Res.string.status_dead
        Status.unknown -> Res.string.status_unknown
    }
}

fun Gender.toStringResource(): StringResource {
    return when (this) {
        Gender.Female -> Res.string.gender_female
        Gender.Male -> Res.string.gender_male
        Gender.Genderless -> Res.string.gender_genderless
        Gender.unknown -> Res.string.gender_unknown
    }
}