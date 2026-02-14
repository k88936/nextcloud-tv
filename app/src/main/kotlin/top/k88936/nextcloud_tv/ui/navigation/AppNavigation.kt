package top.k88936.nextcloud_tv.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import top.k88936.nextcloud_tv.ui.screens.files.FilesScreen
import top.k88936.nextcloud_tv.ui.screens.music.MusicScreen
import top.k88936.nextcloud_tv.ui.screens.photos.PhotosScreen
import top.k88936.nextcloud_tv.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    var selectedIndex by remember { mutableIntStateOf(0) }

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
                Screens.items.forEachIndexed { index, screen ->
                    NavigationDrawerItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(Screens.items[index].route)
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
                navController = navController,
                startDestination = Screens.startDestination.route
            ) {
                composable(Screens.Files.route) {
                    FilesScreen()
                }
                composable(Screens.Photos.route) {
                    PhotosScreen()
                }
                composable(Screens.Music.route) {
                    MusicScreen()
                }
                composable(Screens.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
