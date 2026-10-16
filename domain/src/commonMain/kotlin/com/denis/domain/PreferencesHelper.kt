package com.denis.domain

import kotlin.reflect.KType
import kotlin.reflect.typeOf

interface PreferencesHelper {
	suspend fun <T : Any> set(key: String, value: T): Result<Unit>
	suspend fun <T : Any> get(key: String, defaultValue: T? = null, type: KType): Result<T?>
	suspend fun clear(): Result<Unit>
	
	enum class Key {
		MY_KEY
	}
}

suspend inline fun <reified T : Any> PreferencesHelper.get(key: String, defaultValue: T? = null): Result<T?> {
	return get(key, defaultValue, typeOf<T>())
}
