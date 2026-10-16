package com.denis.rickmorty.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun shouldPaginate(lazyState: LazyListState, list: List<Any>, totalItems: Int): State<Boolean> {
    val layoutInfo by remember { derivedStateOf { lazyState.layoutInfo } }
    val shouldPaginate = remember { mutableStateOf(false) }

    LaunchedEffect(list, layoutInfo) {
        if (list.isEmpty()) {
            shouldPaginate.value = false
            return@LaunchedEffect
        }

        val pendingItems = !lazyState.canScrollForward && totalItems > layoutInfo.totalItemsCount
        shouldPaginate.value = pendingItems
    }
    return shouldPaginate
}

@Composable
fun shouldPaginate(lazyState: LazyGridState, list: List<Any>, totalItems: Int): State<Boolean> {
    val layoutInfo by remember { derivedStateOf { lazyState.layoutInfo } }
    val shouldPaginate = remember { mutableStateOf(false) }

    LaunchedEffect(list, layoutInfo) {
        if (list.isEmpty()) {
            shouldPaginate.value = false
            return@LaunchedEffect
        }

        val pendingItems = !lazyState.canScrollForward && totalItems > layoutInfo.totalItemsCount
        shouldPaginate.value = pendingItems
    }
    return shouldPaginate
}

@Composable
fun SaveListScrollState(
    lazyState: LazyListState,
    scrollState: MutableState<Int>,
    headers: Int = 0
) {
    LaunchedEffect(lazyState, scrollState) {
        snapshotFlow { 
            val index = lazyState.firstVisibleItemIndex
            if (index < headers) 0 else index
        }
            .distinctUntilChanged()
            .collectLatest { index ->
                scrollState.value = index
            }
    }
}

@Composable
fun SaveGridScrollState(
    lazyState: LazyGridState,
    scrollState: MutableState<Int>,
    headers: Int = 0
) {
    LaunchedEffect(lazyState, scrollState) {
        snapshotFlow { 
            val index = lazyState.firstVisibleItemIndex
            if (index < headers) 0 else index
        }
            .distinctUntilChanged()
            .collectLatest { index ->
                scrollState.value = index
            }
    }
}