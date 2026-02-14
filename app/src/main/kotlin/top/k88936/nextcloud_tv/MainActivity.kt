package top.k88936.nextcloud_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.darkColorScheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import top.k88936.nextcloud_tv.di.appModules
import top.k88936.nextcloud_tv.data.repository.AuthRepository
import top.k88936.nextcloud_tv.data.repository.AuthState
import top.k88936.nextcloud_tv.ui.auth.AuthScreen
import top.k88936.nextcloud_tv.ui.navigation.AppNavigation
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        startKoin {
            androidLogger()
            androidContext(this@MainActivity)
            modules(appModules)
        }
        
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                MainContent()
            }
        }
    }
}

@Composable
private fun MainContent(
    authRepository: AuthRepository = koinInject()
) {
    val authState by authRepository.authState.collectAsState()
    
    when (authState) {
        is AuthState.Initializing -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...")
            }
        }
        is AuthState.Unauthenticated -> {
            AuthScreen()
        }
        is AuthState.Authenticated -> {
            AppNavigation(modifier = Modifier.fillMaxSize())
        }
    }
}
