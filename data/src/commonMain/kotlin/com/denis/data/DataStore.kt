package com.denis.data

import org.koin.core.module.Module

expect val platformDataModule: Module

internal const val DATA_STORE_FILE_NAME = "rick_morty.preferences_pb"
