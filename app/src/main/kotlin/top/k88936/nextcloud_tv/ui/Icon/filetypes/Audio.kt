package top.k88936.nextcloud_tv.ui.Icon.filetypes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Audio: ImageVector
    get() {
        if (_Audio != null) {
            return _Audio!!
        }
        _Audio = ImageVector.Builder(
            name = "Audio",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            materialPath {
                moveTo(6.333f, 15.5f)
                curveToRelative(-0.917f, 0f, -1.702f, -0.327f, -2.354f, -0.979f)
                curveTo(3.327f, 13.868f, 3f, 13.083f, 3f, 12.167f)
                reflectiveCurveToRelative(0.327f, -1.702f, 0.979f, -2.354f)
                curveTo(4.632f, 9.16f, 5.417f, 8.833f, 6.333f, 8.833f)
                curveToRelative(0.319f, 0f, 0.615f, 0.038f, 0.886f, 0.114f)
                curveToRelative(0.271f, 0.077f, 0.531f, 0.192f, 0.781f, 0.344f)
                lineTo(8f, 0.5f)
                lineToRelative(5f, 0f)
                lineToRelative(0f, 3.333f)
                lineToRelative(-3.333f, 0f)
                lineToRelative(0f, 8.333f)
                curveToRelative(0f, 0.917f, -0.327f, 1.702f, -0.979f, 2.354f)
                curveTo(8.035f, 15.173f, 7.25f, 15.5f, 6.333f, 15.5f)
                close()
            }
        }.build()

        return _Audio!!
    }

@Suppress("ObjectPropertyName")
private var _Audio: ImageVector? = null
