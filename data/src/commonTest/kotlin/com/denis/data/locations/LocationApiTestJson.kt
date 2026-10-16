package com.denis.data.locations

class LocationApiTestJson {
    val getAllLocations = """
        {
          "info": {
            "count": 126,
            "pages": 7,
            "next": "https://rickandmortyapi.com/api/location?page=2",
            "prev": null
          },
          "results": [
            {
              "id": 1,
              "name": "Earth",
              "type": "Planet",
              "dimension": "Dimension C-137",
              "residents": [
                "https://rickandmortyapi.com/api/character/1",
                "https://rickandmortyapi.com/api/character/2"
              ],
              "url": "https://rickandmortyapi.com/api/location/1",
              "created": "2017-11-10T12:42:04.162Z"
            }
          ]
        }
    """.trimIndent()
    val getSingleLocation = """
        {
          "id": 3,
          "name": "Citadel of Ricks",
          "type": "Space station",
          "dimension": "unknown",
          "residents": [
            "https://rickandmortyapi.com/api/character/8",
            "https://rickandmortyapi.com/api/character/14"
          ],
          "url": "https://rickandmortyapi.com/api/location/3",
          "created": "2017-11-10T13:08:13.191Z"
        }
    """.trimIndent()
    val getMultipleLocations = """
        [
          {
            "id": 3,
            "name": "Citadel of Ricks",
            "type": "Space station",
            "dimension": "unknown",
            "residents": [
              "https://rickandmortyapi.com/api/character/8",
              "https://rickandmortyapi.com/api/character/14"
            ],
            "url": "https://rickandmortyapi.com/api/location/3",
            "created": "2017-11-10T13:08:13.191Z"
          },
          {
            "id": 21,
            "name": "Testicle Monster Dimension",
            "type": "Dimension",
            "dimension": "Testicle Monster Dimension",
            "residents": [
              "https://rickandmortyapi.com/api/character/7",
              "https://rickandmortyapi.com/api/character/436"
            ],
            "url": "https://rickandmortyapi.com/api/location/21",
            "created": "2017-11-18T19:41:01.605Z"
          }
        ]
    """.trimIndent()
}