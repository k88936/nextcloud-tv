package top.k88936.nextcloud_tv.ui.Icon.memories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Favourites: ImageVector
    get() {
        if (_Favourites != null) {
            return _Favourites!!
        }
        _Favourites = ImageVector.Builder(
            name = "Favourites",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            materialPath {
                moveTo(12f, 17.27f)
                lineTo(18.18f, 21f)
                lineTo(16.54f, 13.97f)
                lineTo(22f, 9.24f)
                lineTo(14.81f, 8.62f)
                lineTo(12f, 2f)
                lineTo(9.19f, 8.62f)
                lineTo(2f, 9.24f)
                lineTo(7.45f, 13.97f)
                lineTo(5.82f, 21f)
                lineTo(12f, 17.27f)
                close()
            }
        }.build()

        return _Favourites!!
    }

@Suppress("ObjectPropertyName")
private var _Favourites: ImageVector? = null
