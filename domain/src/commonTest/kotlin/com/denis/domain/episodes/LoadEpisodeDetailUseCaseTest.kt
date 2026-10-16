package com.denis.domain.episodes

import com.denis.domain.FakeCharactersRepository
import com.denis.domain.FakeEpisodesRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoadEpisodeDetailUseCaseTest {

    private lateinit var episodesRepository: FakeEpisodesRepository
    private lateinit var charactersRepository: FakeCharactersRepository
    private lateinit var useCase: LoadEpisodeDetailUseCase

    @BeforeTest
    fun setUp() {
        episodesRepository = FakeEpisodesRepository()
        charactersRepository = FakeCharactersRepository()
        useCase = LoadEpisodeDetailUseCase(episodesRepository, charactersRepository)
    }

    @Test
    fun `getEpisode success saves result`() = runTest {
        val episode = EpisodeModel(id = 1, name = "Pilot")
        episodesRepository.getEpisodeResult = Result.success(episode)

        useCase.getEpisode(1)

        val result = useCase.getEpisode(1) {
            assertTrue(this.isSuccess)
            assertEquals(episode, this.getOrNull())
        }
    }

    @Test
    fun `checkCharactersUrls extracts ids correctly`() = runTest {
        val episode = EpisodeModel(
            id = 1, 
            characters = listOf(
                "https://rickandmortyapi.com/api/character/1",
                "https://rickandmortyapi.com/api/character/2"
            )
        )
        episodesRepository.getEpisodeResult = Result.success(episode)

        useCase.getEpisode(1)
        useCase.checkCharactersUrls {
            assertTrue(this.isSuccess)
            assertEquals(listOf(1, 2), this.getOrNull())
        }
    }

    @Test
    fun `getCharacters returns characters for extracted ids`() = runTest {
        val episode = EpisodeModel(
            id = 1, 
            characters = listOf("https://rickandmortyapi.com/api/character/1")
        )
        val characters = listOf(CharacterModel(id = 1, name = "Rick"))
        
        episodesRepository.getEpisodeResult = Result.success(episode)
        charactersRepository.getCharactersListResult = Result.success(characters)

        useCase.getEpisode(1)
        useCase.checkCharactersUrls()
        useCase.getCharacters {
            assertTrue(this.isSuccess)
            assertEquals(characters, this.getOrNull())
        }
    }

    @Test
    fun `stopOnError prevents further execution`() = runTest {
        episodesRepository.getEpisodeResult = Result.failure(Exception("Network error"))

        useCase.getEpisode(1)
        
        // This should not execute repository call because of stopOnError in getEpisode implementation 
        // (Wait, LoadEpisodeDetailUseCase.kt calls .stopOnError() at the end of getEpisode)
        
        var charactersCalled = false
        useCase.getCharacters {
            charactersCalled = true
        }
        
        assertTrue(!charactersCalled)
    }
}
