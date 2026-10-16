package com.denis.data.characters

class CharactersApiTestJson {
    val getAllCharacters = """
        {
          "info": {
            "count": 826,
            "pages": 42,
            "next": "https://rickandmortyapi.com/api/character/?page=2",
            "prev": null
          },
          "results": [
            {
              "id": 1,
              "name": "Rick Sanchez",
              "status": "Alive",
              "species": "Human",
              "type": "",
              "gender": "Male",
              "origin": {
                "name": "Earth",
                "url": "https://rickandmortyapi.com/api/location/1"
              },
              "location": {
                "name": "Earth",
                "url": "https://rickandmortyapi.com/api/location/20"
              },
              "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
              "episode": [
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2"
              ],
              "url": "https://rickandmortyapi.com/api/character/1",
              "created": "2017-11-04T18:48:46.250Z"
            }
          ]
        }
    """.trimIndent()
    val getSingleCharacter = """
        {
          "id": 2,
          "name": "Morty Smith",
          "status": "Alive",
          "species": "Human",
          "type": "",
          "gender": "Male",
          "origin": {
            "name": "Earth",
            "url": "https://rickandmortyapi.com/api/location/1"
          },
          "location": {
            "name": "Earth",
            "url": "https://rickandmortyapi.com/api/location/20"
          },
          "image": "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
          "episode": [
            "https://rickandmortyapi.com/api/episode/1",
            "https://rickandmortyapi.com/api/episode/2"
          ],
          "url": "https://rickandmortyapi.com/api/character/2",
          "created": "2017-11-04T18:50:21.651Z"
        }
    """.trimIndent()
    val getMultipleCharacters = """
        [
          {
            "id": 1,
            "name": "Rick Sanchez",
            "status": "Alive",
            "species": "Human",
            "type": "",
            "gender": "Male",
            "origin": {
              "name": "Earth (C-137)",
              "url": "https://rickandmortyapi.com/api/location/1"
            },
            "location": {
              "name": "Earth (Replacement Dimension)",
              "url": "https://rickandmortyapi.com/api/location/20"
            },
            "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            "episode": [
              "https://rickandmortyapi.com/api/episode/1",
              "https://rickandmortyapi.com/api/episode/2"
            ],
            "url": "https://rickandmortyapi.com/api/character/1",
            "created": "2017-11-04T18:48:46.250Z"
          },
          {
            "id": 183,
            "name": "Johnny Depp",
            "status": "Alive",
            "species": "Human",
            "type": "",
            "gender": "Male",
            "origin": {
              "name": "Earth (C-500A)",
              "url": "https://rickandmortyapi.com/api/location/23"
            },
            "location": {
              "name": "Earth (C-500A)",
              "url": "https://rickandmortyapi.com/api/location/23"
            },
            "image": "https://rickandmortyapi.com/api/character/avatar/183.jpeg",
            "episode": [
              "https://rickandmortyapi.com/api/episode/8"
            ],
            "url": "https://rickandmortyapi.com/api/character/183",
            "created": "2017-12-29T18:51:29.693Z"
          }
        ]
    """.trimIndent()
}