package com.denis.domain.episodes

import com.denis.domain.BaseUseCase
import com.denis.domain.characters.CharactersRepository
import com.denis.domain.model.characters.CharacterModel
import com.denis.domain.model.episodes.EpisodeModel

class LoadEpisodeDetailUseCase(
    private val episodesRepository: EpisodesRepository,
    private val charactersRepository: CharactersRepository
): BaseUseCase() {
    suspend fun getEpisode(id: Int, send: Result<EpisodeModel>.() -> Unit = {}): LoadEpisodeDetailUseCase {
        clear()
        return execute<LoadEpisodeDetailUseCase> {
            val result = episodesRepository.getEpisode(id)
            save(result)
            send(result)
        }.stopOnError()
    }

    suspend fun checkCharactersUrls(send: Result<List<Int>>.() -> Unit = {}): LoadEpisodeDetailUseCase {
        return execute {
            val ids = runCatching {
                val characterUrls: List<String> = lastResult<EpisodeModel>().getOrThrow().characters
                val ids = characterUrls.mapNotNull { it.split("/").lastOrNull()?.toIntOrNull() }
                if (ids.isEmpty())
                    throw Exception("No characters found")
                ids
            }
            save(ids)
            send(ids)
        }
    }

    suspend fun getCharacters(send: Result<List<CharacterModel>>.() -> Unit = {}): LoadEpisodeDetailUseCase {
        val ids = lastResult<List<Int>>().getOrNull() ?: return this
        return execute {
            send(charactersRepository.getCharacters(ids))
        }
    }
}
