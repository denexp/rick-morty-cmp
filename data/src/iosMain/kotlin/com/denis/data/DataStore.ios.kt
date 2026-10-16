package com.denis.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import okio.Path.Companion.toPath
import org.koin.dsl.module

@OptIn(ExperimentalForeignApi::class)
fun createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory: NSURL = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )!!
            (documentDirectory.path + "/" + DATA_STORE_FILE_NAME).toPath()
        }
    )

actual val platformDataModule = module {
    single<DataStore<Preferences>> { createDataStore() }
}
