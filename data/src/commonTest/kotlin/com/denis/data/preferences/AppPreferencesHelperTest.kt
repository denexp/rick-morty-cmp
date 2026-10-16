package com.denis.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.denis.data.AppPreferencesHelper
import com.denis.domain.get
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppPreferencesHelperTest {

    private lateinit var testScope: TestScope
    private lateinit var testDataStorePath: okio.Path
    private val fileSystem = FileSystem.SYSTEM
    private lateinit var helper: AppPreferencesHelper

    @BeforeTest
    fun setUp() {
        testScope = TestScope(UnconfinedTestDispatcher())
        testDataStorePath = "test_prefs_${Clock.System.now().toEpochMilliseconds()}.preferences_pb".toPath()
        if (fileSystem.exists(testDataStorePath)) {
            fileSystem.delete(testDataStorePath)
        }
        val dataStore = PreferenceDataStoreFactory.createWithPath(
            scope = testScope,
            produceFile = { testDataStorePath }
        )
        helper = AppPreferencesHelper(dataStore)
    }

    @AfterTest
    fun tearDown() {
        testScope.cancel()
        if (fileSystem.exists(testDataStorePath)) {
            fileSystem.delete(testDataStorePath)
        }
    }

    @Test
    fun `set and get String works`() = runTest {
        val key = "test_key"
        val value = "test_value"
        
        val setResult = helper.set(key, value)
        assertTrue(setResult.isSuccess, "Set should be successful: ${setResult.exceptionOrNull()}")
        
        val result = helper.get<String>(key)
        
        assertTrue(result.isSuccess, "Get should be successful: ${result.exceptionOrNull()}")
        assertEquals(value, result.getOrNull())
    }

    @Test
    fun `set and get Instant works`() = runTest {
        val key = "time_key"
        val value = Instant.fromEpochMilliseconds(123456789L)
        
        helper.set(key, value)
        val result = helper.get(key, Instant.fromEpochMilliseconds(0L))
        
        assertTrue(result.isSuccess)
        assertEquals(value, result.getOrNull())
    }

    @Test
    fun `set and get Int works`() = runTest {
        val key = "int_key"
        val value = 42
        
        helper.set(key, value)
        val result = helper.get(key, 0)
        
        assertTrue(result.isSuccess)
        assertEquals(value, result.getOrNull())
    }

    @Test
    fun `clear removes all values`() = runTest {
        helper.set("key1", "value1")
        helper.set("key2", 2)
        
        helper.clear()
        
        val val1 = helper.get("key1", "")
        val val2 = helper.get("key2", 0)
        
        assertEquals("", val1.getOrNull())
        assertEquals(0, val2.getOrNull())
    }
}
