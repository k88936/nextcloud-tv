package top.k88936.nextcloud_tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import top.k88936.nextcloud_tv.ui.auth.AuthScreen

private val NextcloudTvColors = darkColorScheme(
    primary = Color(0xFF0096A6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFAA3F),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFFFFAA3F),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFAA3F),
    onSecondaryContainer = Color.Black,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    inverseSurface = Color(0xFFFFAA3F),
    inverseOnSurface = Color.Black
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = NextcloudTvColors) {
                AuthScreen()
            }
        }
    }
}