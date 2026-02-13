package top.k88936.nextcloud_tv.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
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
import androidx.tv.material3.NavigationDrawerScope
import androidx.tv.material3.Text
import androidx.tv.material3.rememberDrawerState
import top.k88936.nextcloud_tv.ui.screens.files.FilesScreen
import top.k88936.nextcloud_tv.ui.screens.home.HomeScreen
import top.k88936.nextcloud_tv.ui.screens.music.MusicScreen
import top.k88936.nextcloud_tv.ui.screens.photos.PhotosScreen
import top.k88936.nextcloud_tv.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    var selectedIndex by remember { mutableIntStateOf(0) }

    NavigationDrawer(
        drawerState=drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background.copy(0.9f))
                    .fillMaxHeight()
                    .padding(12.dp)
                    .selectableGroup(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Screen.items.forEachIndexed { index, screen ->
                    NavigationDrawerItem(
                        selected = selectedIndex == index,
                        onClick = {
                            selectedIndex = index
                            navController.navigate(Screen.items[index].route)
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
        NavHost(
            navController = navController,
            startDestination = Screen.startDestination.route
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Files.route) {
                FilesScreen()
            }
            composable(Screen.Photos.route) {
                PhotosScreen()
            }
            composable(Screen.Music.route) {
                MusicScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
