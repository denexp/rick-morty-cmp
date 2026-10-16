package com.denis.data.episodes

import com.denis.data.entity.EpisodeEntity
import com.denis.data.entity.EpisodesEntity
import com.denis.data.entity.InfoEntity
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class EpisodesMapperTest {

    private lateinit var mapper: EpisodesMapper

    @BeforeTest
    fun setUp() {
        mapper = EpisodesMapper()
    }

    @Test
    fun `toModel with EpisodesEntity should map all fields correctly`() {
        val entity = EpisodesEntity(
            info = InfoEntity(count = 5, pages = 1, next = "next", prev = null),
            results = listOf(
                createEpisodeEntity(1, "Pilot"),
                createEpisodeEntity(2, "Lawnmower Dog")
            )
        )

        val model = mapper.toModel(entity)

        assertEquals(5, model.info.count)
        assertEquals(2, model.results.size)
        assertEquals(1, model.results[0].id)
        assertEquals("Pilot", model.results[0].name)
    }

    @Test
    fun `toModel with EpisodeEntity should map all fields correctly`() {
        val entity = EpisodeEntity(
            id = 1,
            name = "Pilot",
            air_date = "December 2, 2013",
            episode = "S01E01",
            characters = listOf("char1", "char2"),
            url = "url",
            created = "created"
        )

        val model = mapper.toModel(entity)

        assertEquals(1, model.id)
        assertEquals("Pilot", model.name)
        assertEquals("December 2, 2013", model.airDate)
        assertEquals("S01E01", model.episode)
        assertEquals(listOf("char1", "char2"), model.characters)
        assertEquals("url", model.url)
        assertEquals("created", model.created)
    }

    private fun createEpisodeEntity(id: Int, name: String) = EpisodeEntity(
        id = id,
        name = name,
        air_date = "",
        episode = "",
        characters = emptyList(),
        url = "",
        created = ""
    )
}
