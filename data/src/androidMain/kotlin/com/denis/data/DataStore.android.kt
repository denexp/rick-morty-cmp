package com.denis.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import org.koin.dsl.module

fun createDataStore(context: Context): DataStore<Preferences> =
    androidx.datastore.preferences.core.PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile(DATA_STORE_FILE_NAME) }
    )

actual val platformDataModule = module {
    single<DataStore<Preferences>> { createDataStore(get()) }
}
