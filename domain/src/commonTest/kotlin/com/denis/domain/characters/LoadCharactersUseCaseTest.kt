package com.denis.domain.characters

import com.denis.domain.FakeCharactersRepository
import com.denis.domain.model.characters.CharactersModel
import com.denis.domain.model.characters.InfoModel
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoadCharactersUseCaseTest {

    private lateinit var repository: FakeCharactersRepository
    private lateinit var useCase: LoadCharactersUseCase

    @BeforeTest
    fun setUp() {
        repository = FakeCharactersRepository()
        useCase = LoadCharactersUseCase(repository)
    }

    @Test
    fun `getCharacters success`() = runTest {
        val expected = CharactersModel(info = InfoModel(count = 1), results = emptyList())
        repository.getCharactersResult = Result.success(expected)

        var actual: Result<CharactersModel>? = null
        useCase.getCharacters(1) { actual = this }

        assertTrue(actual!!.isSuccess)
        assertEquals(expected, actual!!.getOrNull())
    }

    @Test
    fun `getCharacters failure`() = runTest {
        val expected = Exception("Error")
        repository.getCharactersResult = Result.failure(expected)

        var actual: Result<CharactersModel>? = null
        useCase.getCharacters(1) { actual = this }

        assertTrue(actual!!.isFailure)
        assertEquals(expected, actual!!.exceptionOrNull())
    }
}
