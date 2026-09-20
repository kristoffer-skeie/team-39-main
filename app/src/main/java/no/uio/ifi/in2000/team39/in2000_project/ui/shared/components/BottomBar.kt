package no.uio.ifi.in2000.team39.in2000_project.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team39.in2000_project.R

/**
 * Represents the navigation bar at the bottom of the screen, providing quick access to major sections
 * of the application. This bar includes icons and labels for home, map, favorites, and contact sections.
 * Navigation is done with use of the [navController].
 */
@Composable
fun BottomBar(
    navController: NavController,
) {
    val colors = MaterialTheme.colorScheme
    val homeRoute = stringResource(R.string.home_screen_route)
    val mapRoute = stringResource(R.string.map_screen_route)
    val favoriteRoute = stringResource(R.string.favorites_screen_route)
    val contactRoute = stringResource(R.string.contact_screen_route)

    BottomAppBar(
        containerColor = colors.surface,
        contentColor = colors.onSurface,
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier.background(colors.surface),
        ) {
            // Home navigation button
            BottomBarItem(
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.home_button_text),
                icon = Icons.Filled.Home,
                contentDescription = stringResource(id = R.string.home_button_description),
                onClick = { navController.navigate(homeRoute) }
            )

            // Map navigation button
            BottomBarItem(
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.map_button_text),
                icon = Icons.Filled.Map,
                contentDescription = stringResource(id = R.string.map_button_description),
                onClick = { navController.navigate(mapRoute) }
            )

            // Favorites navigation button
            BottomBarItem(
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.favorites_button_text),
                icon = Icons.Filled.Favorite,
                contentDescription = stringResource(id = R.string.favorites_button_description),
                onClick = { navController.navigate(favoriteRoute) }
            )

            // Contact navigation button
            BottomBarItem(
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.contact_button_text),
                icon = Icons.Filled.Email,
                contentDescription = stringResource(id = R.string.contact_button_description),
                onClick = { navController.navigate(contactRoute) }
            )
        }
    }
}

/**
 * Represents a single item in the BottomBar with an icon and a label, providing a clickable area
 * with appropriate padding and size for accessibility. The [label] parameter specifies the text to
 * display below the icon, the [icon] parameter provides the vector icon to display, the
 * [contentDescription] parameter describes the icon for accessibility purposes, and the [onClick]
 * parameter defines the action to perform when the item is clicked, and the [modifier] parameter
 * allows for custom styling and positioning.
 */
@Composable
fun BottomBarItem(
    modifier: Modifier,
    label: String,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick)
            .defaultMinSize(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
            )

            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
    }
}
