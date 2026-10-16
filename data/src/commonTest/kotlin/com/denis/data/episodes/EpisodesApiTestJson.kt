package com.denis.data.episodes

class EpisodesApiTestJson {
    val getAllEpisodes = """
        {
          "info": {
            "count": 51,
            "pages": 3,
            "next": "https://rickandmortyapi.com/api/episode?page=2",
            "prev": null
          },
          "results": [
            {
              "id": 1,
              "name": "Pilot",
              "air_date": "December 2, 2013",
              "episode": "S01E01",
              "characters": [
                "https://rickandmortyapi.com/api/character/1",
                "https://rickandmortyapi.com/api/character/2"
              ],
              "url": "https://rickandmortyapi.com/api/episode/1",
              "created": "2017-11-10T12:56:33.798Z"
            }
          ]
        }
    """.trimIndent()
    val getSingleEpisode = """
        {
          "id": 28,
          "name": "The Ricklantis Mixup",
          "air_date": "September 10, 2017",
          "episode": "S03E07",
          "characters": [
            "https://rickandmortyapi.com/api/character/1",
            "https://rickandmortyapi.com/api/character/2"
          ],
          "url": "https://rickandmortyapi.com/api/episode/28",
          "created": "2017-11-10T12:56:36.618Z"
        }
    """.trimIndent()
    val getMultipleEpisodes = """
        [
          {
            "id": 10,
            "name": "Close Rick-counters of the Rick Kind",
            "air_date": "April 7, 2014",
            "episode": "S01E10",
            "characters": [
              "https://rickandmortyapi.com/api/character/1",
              "https://rickandmortyapi.com/api/character/2"
            ],
            "url": "https://rickandmortyapi.com/api/episode/10",
            "created": "2017-11-10T12:56:34.747Z"
          },
          {
            "id": 28,
            "name": "The Ricklantis Mixup",
            "air_date": "September 10, 2017",
            "episode": "S03E07",
            "characters": [
              "https://rickandmortyapi.com/api/character/1",
              "https://rickandmortyapi.com/api/character/2"
            ],
            "url": "https://rickandmortyapi.com/api/episode/28",
            "created": "2017-11-10T12:56:36.618Z"
          }
        ]
    """.trimIndent()
}