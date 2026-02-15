package top.k88936.nextcloud_tv.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Files
import androidx.compose.material.icons.filled.Photos
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings2
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
)

object Screens {
    data object Files : Screen(
        route = "files",
        title = "Files",
        icon = Icons.Filled.Files
    )

    data object Photos : Screen(
        route = "photos",
        title = "Photos",
        icon = Icons.Filled.Photos
    )

    data object Music : Screen(
        route = "music",
        title = "Music",
        icon = Icons.Filled.PlayArrow
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Filled.Settings2
    )

    val items = listOf(Files, Photos, Music, Settings)
    val startDestination = Files
}
