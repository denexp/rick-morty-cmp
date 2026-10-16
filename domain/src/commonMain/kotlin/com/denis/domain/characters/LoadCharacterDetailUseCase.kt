package com.denis.domain.characters

import com.denis.domain.BaseUseCase
import com.denis.domain.episodes.EpisodesRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel

class LoadCharacterDetailUseCase(
    private val charactersRepository: CharactersRepository,
    private val episodesRepository: EpisodesRepository
) : BaseUseCase() {

    suspend fun getCharacter(id: Int, send: Result<CharacterModel>.() -> Unit = {}): LoadCharacterDetailUseCase {
        clear()
        return execute<LoadCharacterDetailUseCase> {
            val result = charactersRepository.getCharacter(id)
            save(result)
            send(result)
        }.stopOnError()
    }

    suspend fun checkEpisodeUrls(send: Result<List<Int>>.() -> Unit = {}): LoadCharacterDetailUseCase {
        return execute {
            val ids = runCatching {
                val episodeIds = lastResult<CharacterModel>().getOrThrow().episodeIds
                if (episodeIds.isEmpty())
                    throw Exception("No episodes found")
                episodeIds
            }
            save(ids)
            send(ids)
        }
    }

    suspend fun getEpisodes(send: Result<List<EpisodeModel>>.() -> Unit = {}): LoadCharacterDetailUseCase {
        val ids = lastResult<List<Int>>().getOrNull() ?: return this
        return execute {
            send(episodesRepository.getEpisodes(ids))
        }
    }
}
