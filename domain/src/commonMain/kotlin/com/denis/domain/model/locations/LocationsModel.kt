package com.denis.domain.model.locations

import com.denis.domain.model.characters.InfoModel

data class LocationsModel(
    val info: InfoModel = InfoModel(),
    val results: List<LocationModel> = emptyList(),
)
