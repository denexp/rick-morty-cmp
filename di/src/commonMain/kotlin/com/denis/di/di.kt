package com.denis.di

import com.denis.data.di.dataModule
import com.denis.data.platformDataModule
import com.denis.domain.di.domainModule

fun layerModules() = listOf(domainModule, dataModule, platformDataModule)
