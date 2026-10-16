package com.denis.data

import com.denis.domain.PreferencesHelper
import kotlin.reflect.KType

class FakePreferencesHelper: PreferencesHelper {
    private val map: MutableMap<String, Any> = mutableMapOf()
    
    override suspend fun <T : Any> set(key: String, value: T): Result<Unit> {
        map[key] = value
        return Result.success(Unit)
    }
    
    override suspend fun <T : Any> get(key: String, defaultValue: T?, type: KType): Result<T?> {
        return Result.success(map[key] as? T)
    }
    
    override suspend fun clear(): Result<Unit> {
        map.clear()
        return Result.success(Unit)
    }
}