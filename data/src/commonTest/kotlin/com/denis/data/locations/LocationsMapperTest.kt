package com.denis.data.locations

import com.denis.data.entity.InfoEntity
import com.denis.data.entity.LocationEntity
import com.denis.data.entity.LocationsEntity
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LocationsMapperTest {

    private lateinit var mapper: LocationsMapper

    @BeforeTest
    fun setUp() {
        mapper = LocationsMapper()
    }

    @Test
    fun `toModel with LocationsEntity should map all fields correctly`() {
        val entity = LocationsEntity(
            info = InfoEntity(count = 20, pages = 2, next = "next", prev = null),
            results = listOf(
                createLocationEntity(1, "Earth"),
                createLocationEntity(2, "Abadango")
            )
        )

        val model = mapper.toModel(entity)

        assertEquals(20, model.info.count)
        assertEquals(2, model.results.size)
        assertEquals(1, model.results[0].id)
        assertEquals("Earth", model.results[0].name)
    }

    @Test
    fun `toModel with LocationEntity should map all fields correctly`() {
        val entity = LocationEntity(
            id = 1,
            name = "Earth (C-137)",
            type = "Planet",
            dimension = "Dimension C-137",
            residents = listOf("res1", "res2"),
            url = "url",
            created = "created"
        )

        val model = mapper.toModel(entity)

        assertEquals(1, model.id)
        assertEquals("Earth (C-137)", model.name)
        assertEquals("Planet", model.type)
        assertEquals("Dimension C-137", model.dimension)
        assertEquals(listOf("res1", "res2"), model.residents)
        assertEquals("url", model.url)
        assertEquals("created", model.created)
    }

    private fun createLocationEntity(id: Int, name: String) = LocationEntity(
        id = id,
        name = name,
        type = "",
        dimension = "",
        residents = emptyList(),
        url = "",
        created = ""
    )
}
