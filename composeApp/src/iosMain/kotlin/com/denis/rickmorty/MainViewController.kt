package com.denis.rickmorty

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(koinModules())
        }
    }
) {
    App()
}