package com.denis.data.characters

import com.denis.data.entity.CharacterEntity
import com.denis.data.entity.CharacterLocationEntity
import com.denis.data.entity.CharactersEntity
import com.denis.data.entity.GenderEntity
import com.denis.data.entity.InfoEntity
import com.denis.data.entity.OriginEntity
import com.denis.data.entity.StatusEntity
import com.denis.domain.model.characters.Gender
import com.denis.domain.model.characters.Status
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CharactersMapperTest {

    private lateinit var mapper: CharactersMapper

    @BeforeTest
    fun setUp() {
        mapper = CharactersMapper()
    }

    @Test
    fun `toModel with CharactersEntity should map all fields correctly`() {
        val entity = CharactersEntity(
            info = InfoEntity(count = 10, pages = 1, next = "next_url", prev = "prev_url"),
            results = listOf(
                createCharacterEntity(1, "Rick"),
                createCharacterEntity(2, "Morty")
            )
        )

        val model = mapper.toModel(entity)

        assertEquals(10, model.info.count)
        assertEquals(1, model.info.pages)
        assertEquals("next_url", model.info.next)
        assertEquals("prev_url", model.info.prev)
        assertEquals(2, model.results.size)
        assertEquals(1, model.results[0].id)
        assertEquals("Rick", model.results[0].name)
        assertEquals(2, model.results[1].id)
        assertEquals("Morty", model.results[1].name)
    }

    @Test
    fun `toModel with CharacterEntity should map all fields correctly`() {
        val entity = CharacterEntity(
            id = 1,
            name = "Rick Sanchez",
            status = StatusEntity.Alive,
            species = "Human",
            type = "",
            gender = GenderEntity.Male,
            origin = OriginEntity("Earth", "origin_url"),
            location = CharacterLocationEntity("Citadel", "location_url"),
            image = "image_url",
            episode = listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2"
            )
        )

        val model = mapper.toModel(entity)

        assertEquals(1, model.id)
        assertEquals("Rick Sanchez", model.name)
        assertEquals(Status.Alive, model.status)
        assertEquals("Human", model.species)
        assertEquals("", model.type)
        assertEquals(Gender.Male, model.gender)
        assertEquals("Earth", model.origin.name)
        assertEquals("origin_url", model.origin.url)
        assertEquals("Citadel", model.location.name)
        assertEquals("location_url", model.location.url)
        assertEquals("image_url", model.image)
        assertEquals(listOf(1, 2), model.episodeIds)
    }

    @Test
    fun `toModel with CharacterEntity should handle empty episode list`() {
        val entity = createCharacterEntity(1, "Rick").copy(episode = null)
        val model = mapper.toModel(entity)
        assertEquals(emptyList(), model.episodeIds)
    }

    private fun createCharacterEntity(id: Int, name: String) = CharacterEntity(
        id = id,
        name = name,
        status = StatusEntity.Alive,
        species = "Human",
        type = "",
        gender = GenderEntity.Male,
        origin = OriginEntity("Earth", ""),
        location = CharacterLocationEntity("Earth", ""),
        image = "",
        episode = emptyList()
    )
}
