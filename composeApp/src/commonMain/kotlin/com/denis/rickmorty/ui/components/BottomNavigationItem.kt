package com.denis.rickmorty.ui.components

import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

data class BottomNavigationItem(
    val index: Int,
    val image: DrawableResource,
    val selectedImage: DrawableResource? = null,
    val selectedColor: Color? = null,
    val label: String? = null,
)