package com.denis.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.denis.domain.Log
import com.denis.domain.PreferencesHelper
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType

class AppPreferencesHelper(
    private val dataStore: DataStore<Preferences>
): PreferencesHelper {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    override suspend fun clear() = Result.runCatching {
        dataStore.edit { preferences ->
            preferences.clear()
        }
        Unit
    }
    
    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> set(key: String, value: T): Result<Unit> = runCatching {
        dataStore.edit { preferences ->
            when(value) {
                is Boolean -> {
                    preferences[booleanPreferencesKey(key)] = value
                }
                
                is Set<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    preferences[stringSetPreferencesKey(key)] = value as Set<String>
                }
                
                is String -> {
                    preferences[stringPreferencesKey(key)] = value
                }
                
                is Float -> {
                    preferences[floatPreferencesKey(key)] = value
                }
                
                is Long -> {
                    preferences[longPreferencesKey(key)] = value
                }
                
                is Int -> {
                    preferences[intPreferencesKey(key)] = value
                }

                is Instant -> {
                    preferences[longPreferencesKey(key)] = value.toEpochMilliseconds()
                }

                else -> {
                    val serializer = value::class.serializer()
                    @Suppress("UNCHECKED_CAST")
                    preferences[stringPreferencesKey(key)] = json.encodeToString(serializer as KSerializer<Any>, value as Any)
                }
            }
        }
    }
    
    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any> get(key: String, defaultValue: T?, type: KType): Result<T?> {
        return runCatching {
            val valueFlow = dataStore.data
                .map { preferences ->
                    val result = when(type.classifier) {
                        Boolean::class -> {
                            preferences[booleanPreferencesKey(key)]
                        }
                        
                        Set::class -> {
                            preferences[stringSetPreferencesKey(key)]
                        }
                        
                        String::class -> {
                            preferences[stringPreferencesKey(key)]
                        }
                        
                        Float::class -> {
                            preferences[floatPreferencesKey(key)]
                        }
                        
                        Long::class -> {
                            preferences[longPreferencesKey(key)]
                        }
                        
                        Int::class -> {
                            preferences[intPreferencesKey(key)]
                        }

                        Instant::class -> {
                            preferences[longPreferencesKey(key)]?.let { Instant.fromEpochMilliseconds(it) }
                        }

                        else -> {
                            val stringValue = preferences[stringPreferencesKey(key)]
                            if (stringValue != null) {
                                val serializer = json.serializersModule.serializer(type)
                                @Suppress("UNCHECKED_CAST")
                                json.decodeFromString(serializer as KSerializer<Any>, stringValue)
                            } else {
                                null
                            }
                        }
                    }
                    @Suppress("UNCHECKED_CAST")
                    (result ?: defaultValue) as? T
                }
            valueFlow.firstOrNull() ?: defaultValue
        }
    }
}
