package com.denis.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BaseUseCaseTest {

    private class TestUseCase : BaseUseCase() {
        suspend fun doWork(success: Boolean) = execute<TestUseCase> {
            if (success) {
                save(Result.success("Success"))
            } else {
                save(Result.failure(Exception("Error")))
            }
        }

        fun getTestResult() = lastResult<String>()

        suspend fun step2() = execute<TestUseCase> {
            save(Result.success("Step 2"))
        }

        fun stopIfError() = stopOnError<TestUseCase>()
    }

    @Test
    fun `execute saves result`() = runTest {
        val useCase = TestUseCase()
        useCase.doWork(true)
        
        val result = useCase.getTestResult()
        assertTrue(result.isSuccess)
        assertEquals("Success", result.getOrNull())
    }

    @Test
    fun `clear resets state`() = runTest {
        val useCase = TestUseCase()
        useCase.doWork(false)
        useCase.stopIfError()
        
        useCase.clear()
        
        useCase.doWork(true)
        val result = useCase.getTestResult()
        assertTrue(result.isSuccess)
        assertEquals("Success", result.getOrNull())
    }

    @Test
    fun `stopOnError prevents further execution`() = runTest {
        val useCase = TestUseCase()
        useCase.doWork(false)
        useCase.stopIfError()
        
        useCase.step2()
        
        val result = useCase.getTestResult()
        assertTrue(result.isFailure)
        assertEquals("Error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `without stopOnError execution continues`() = runTest {
        val useCase = TestUseCase()
        useCase.doWork(false)
        
        // No stopOnError called
        
        useCase.step2()
        
        val result = useCase.getTestResult()
        assertTrue(result.isSuccess)
        assertEquals("Step 2", result.getOrNull())
    }
}
