package top.k88936.nextcloud_tv.ui.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Files
import androidx.compose.material.icons.filled.Settings2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.NavigationDrawer
import androidx.tv.material3.NavigationDrawerItem
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import top.k88936.nextcloud_tv.ui.Icon.Memories
import top.k88936.nextcloud_tv.ui.app.files.FilesScreen
import top.k88936.nextcloud_tv.ui.app.memories.MemoriesScreen
import top.k88936.nextcloud_tv.ui.app.settings.SettingsScreen

object ScreensConfig {
    sealed class Screen(
        val route: String,
        val title: String,
        val icon: ImageVector
    )

    data object Files : Screen(
        route = "files",
        title = "Files",
        icon = Icons.Filled.Files
    )

    data object Memories : Screen(
        route = "memories",
        title = "Memories",
        icon = Icons.Filled.Memories
    )


    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Filled.Settings2
    )

    val items = listOf(Files, Memories, Settings)
    val startDestination = Files
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    var selectedIndex by remember { mutableIntStateOf(0) }
    val innerNavController = rememberNavController()

    NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.copy(0.98f))
                    .fillMaxHeight()
                    .padding(12.dp)
                    .selectableGroup(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ScreensConfig.items.forEachIndexed { index, screen ->
                    NavigationDrawerItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            innerNavController.navigate(ScreensConfig.items[index].route) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        leadingContent = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = null
                            )
                        }
                    ) {
                        Text(screen.title)
                    }
                }

            }
        },
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = SurfaceDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
        ) {

            NavHost(
                navController = innerNavController,
                startDestination = ScreensConfig.startDestination.route
            ) {
                composable(ScreensConfig.Files.route) {
                    FilesScreen()
                }
                composable(ScreensConfig.Memories.route) {
                    MemoriesScreen()
                }
                composable(ScreensConfig.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
