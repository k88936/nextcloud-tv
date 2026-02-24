package top.k88936.nextcloud_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.data.repository.ClientRepository
import top.k88936.nextcloud_tv.di.appModules
import top.k88936.nextcloud_tv.navigation.LocalNavController
import top.k88936.nextcloud_tv.ui.app.AppNavigation
import top.k88936.nextcloud_tv.ui.auth.AuthScreen
import top.k88936.nextcloud_tv.ui.modal.PhotoViewerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModules)
        }

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val navController = rememberNavController()
                val clientRepository: ClientRepository = koinInject()
                val authState by clientRepository.authState.collectAsState()
                var isInitializing by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    clientRepository.login()
                    isInitializing = false
                }

                LaunchedEffect(authState, isInitializing) {
                    if (isInitializing) return@LaunchedEffect
                    
                    when (authState) {
                        is AuthState.Authenticated -> {
                            if (navController.currentDestination?.route == "auth") {
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        }

                        is AuthState.Unauthenticated -> {
                            if (navController.currentDestination?.route != "auth") {
                                navController.navigate("auth") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }

                if (isInitializing) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.splash),
                            contentDescription = "Splash",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    CompositionLocalProvider(LocalNavController provides navController) {
                        NavHost(
                            navController = navController,
                            startDestination = "main",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            composable("auth") {
                                AuthScreen()
                            }
                            composable("main") {
                                AppNavigation(
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            composable(
                                route = "photo_viewer?url={url}&name={name}",
                                arguments = listOf(
                                    navArgument("url") { type = NavType.StringType },
                                    navArgument("name") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val url = backStackEntry.arguments?.getString("url") ?: ""
                                val name = backStackEntry.arguments?.getString("name") ?: ""
                                PhotoViewerScreen(
                                    url = url,
                                    name = name,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
