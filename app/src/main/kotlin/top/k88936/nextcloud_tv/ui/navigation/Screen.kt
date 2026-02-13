package top.k88936.nextcloud_tv.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Files : Screen(
        route = "files",
        title = "Files",
        icon = Icons.Default.Star
    )

    data object Photos : Screen(
        route = "photos",
        title = "Photos",
        icon = Icons.Default.Person
    )

    data object Music : Screen(
        route = "music",
        title = "Music",
        icon = Icons.Default.Info
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )

    companion object {
        val items = listOf(Home, Files, Photos, Music, Settings)
        val startDestination = Home
    }
}
