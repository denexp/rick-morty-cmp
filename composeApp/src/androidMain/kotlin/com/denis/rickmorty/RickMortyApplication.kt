package com.denis.rickmorty

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class RickMortyApplication: Application() {
    override fun onCreate() {
        startKoin {
            androidContext(this@RickMortyApplication)
            modules(koinModules())
        }
        super.onCreate()
    }
}