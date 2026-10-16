package com.denis.rickmorty.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomNavigationBarComponent(
    id: String,
    items: List<BottomNavigationItem>,
    itemSelectedId: Int,
    underlineSelected: Boolean = true,
    onItemClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.background(color = MaterialTheme.colorScheme.surface).fillMaxWidth().testTag(id)
    ) {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            items.forEach {
                NavigationItemComponent(
                    modifier = Modifier.weight(1f),
                    data = it,
                    underlineSelected = underlineSelected,
                    selected = itemSelectedId == it.index,
                    onClick = onItemClick
                )
            }
        }
    }
}

@OptIn(InternalResourceApi::class)
@Composable
private fun NavigationItemComponent(
    modifier: Modifier,
    data: BottomNavigationItem,
    underlineSelected: Boolean,
    selected: Boolean,
    onClick: (Int) -> Unit
) {
    val tintColor = bottomNavigationItemColor(selected, data)

    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            onClick(data.index)
        }.then(modifier),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .size(30.dp)
                .testTag("NavigationItemIcon${data.index}"),
            painter = if (selected && data.selectedImage != null)
                painterResource(data.selectedImage)
            else
                painterResource(data.image),
            contentDescription = data.label,
            tint = tintColor,
        )
        if (!data.label.isNullOrEmpty())
            Text(
                modifier = Modifier,
                text = data.label,
                color = tintColor,
            )
        if(underlineSelected && selected) {
            HorizontalDivider(
                modifier = Modifier
                    .height(4.dp)
                    .width(8.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun bottomNavigationItemColor(
    selected: Boolean,
    data: BottomNavigationItem
): Color {
    val useSelectedColor = selected && data.selectedColor != null
    val selectedColor = if (useSelectedColor) data.selectedColor else MaterialTheme.colorScheme.onSurface
    return if (useSelectedColor) selectedColor else MaterialTheme.colorScheme.onSurface
}