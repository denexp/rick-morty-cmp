package com.denis.domain.characters

import com.denis.domain.FakeCharactersRepository
import com.denis.domain.FakeEpisodesRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoadCharacterDetailUseCaseTest {

    private lateinit var charactersRepository: FakeCharactersRepository
    private lateinit var episodesRepository: FakeEpisodesRepository
    private lateinit var useCase: LoadCharacterDetailUseCase

    private val character = CharacterModel(
        id = 1,
        name = "Rick Sanchez",
        episodeIds = listOf(1)
    )

    private val episode = EpisodeModel(
        id = 1,
        name = "Pilot",
        airDate = "December 2, 2013",
        episode = "S01E01",
        characters = listOf("https://rickandmortyapi.com/api/character/1"),
        url = "url",
        created = "date"
    )

    @BeforeTest
    fun setUp() {
        charactersRepository = FakeCharactersRepository()
        episodesRepository = FakeEpisodesRepository()
        useCase = LoadCharacterDetailUseCase(charactersRepository, episodesRepository)
    }

    @Test
    fun `full flow success`() = runTest {
        charactersRepository.getCharacterResult = Result.success(character)
        episodesRepository.getEpisodesListResult = Result.success(listOf(episode))

        var characterResult: Result<CharacterModel>? = null
        var idsResult: Result<List<Int>>? = null
        var episodesResult: Result<List<EpisodeModel>>? = null

        useCase.getCharacter(1) { characterResult = this }
            .checkEpisodeUrls { idsResult = this }
            .getEpisodes { episodesResult = this }

        assertTrue(characterResult!!.isSuccess)
        assertEquals(character, characterResult!!.getOrNull())
        assertEquals(listOf(1), idsResult!!.getOrNull())
        assertEquals(listOf(episode), episodesResult!!.getOrNull())
    }

    @Test
    fun `getCharacter failure stops execution`() = runTest {
        charactersRepository.getCharacterResult = Result.failure(Exception("Network error"))

        var characterResult: Result<CharacterModel>? = null
        var idsResult: Result<List<Int>>? = null

        useCase.getCharacter(1) { characterResult = this }
            .checkEpisodeUrls { idsResult = this }

        assertTrue(characterResult!!.isFailure)
        assertEquals(null, idsResult)
    }

    @Test
    fun `checkEpisodeUrls failure stops execution`() = runTest {
        charactersRepository.getCharacterResult = Result.success(character.copy(episodeIds = emptyList()))

        var idsResult: Result<List<Int>>? = null
        var episodesResult: Result<List<EpisodeModel>>? = null

        useCase.getCharacter(1)
            .checkEpisodeUrls { idsResult = this }
            .getEpisodes { episodesResult = this }

        assertTrue(idsResult!!.isFailure)
        assertEquals(null, episodesResult)
    }
}
