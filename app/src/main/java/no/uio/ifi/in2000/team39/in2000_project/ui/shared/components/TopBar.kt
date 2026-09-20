package no.uio.ifi.in2000.team39.in2000_project.ui.shared.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team39.in2000_project.R

/**
 * Provides a reusable top application bar with optional back and settings icons. It is designed to
 * provide consistent navigation and settings access across different screens. The [onBackClick] parameter
 * is an optional callback for handling back navigation, displaying a back arrow when provided. The [onSettingsClick]
 * parameter is an optional callback for handling navigation to the settings screen, displaying a settings
 * icon when provided. The [title] parameter specifies the text to be displayed as the title in the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    onBackClick: (() -> Unit)? = null,
    onSettingsClick: (() -> Unit)? = null,
    title: String,
) {
    val colors = MaterialTheme.colorScheme

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colors.surface,
            titleContentColor = colors.onSurface,
            actionIconContentColor = colors.onSurface,
            navigationIconContentColor = colors.onSurface
        ),
        title = {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() }
            )
        },
        navigationIcon = {
            onBackClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button_description),
                        modifier = Modifier
                            .padding(5.dp)
                            .defaultMinSize(48.dp),
                    )
                }
            }
        },
        actions = {
            onSettingsClick?.let {
                IconButton(onClick = { onSettingsClick() }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = stringResource(id = R.string.settings_button_description),
                        modifier = Modifier
                            .padding(5.dp)
                            .defaultMinSize(48.dp),
                    )
                }
            }
        }
    )
}
