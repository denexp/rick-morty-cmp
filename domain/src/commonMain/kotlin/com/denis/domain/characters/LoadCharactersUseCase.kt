package com.denis.domain.characters

import com.denis.domain.BaseUseCase
import com.denis.domain.model.characters.CharactersModel

class LoadCharactersUseCase(
    private val repository: CharactersRepository
) : BaseUseCase() {

    suspend fun getCharacters(
        page: Int = 1,
        name: String? = null,
        send: Result<CharactersModel>.() -> Unit = {}
    ): LoadCharactersUseCase {
        clear()
        return execute {
            val result = repository.getCharacters(page, name)
            save(result)
            send(result)
        }
    }
}
