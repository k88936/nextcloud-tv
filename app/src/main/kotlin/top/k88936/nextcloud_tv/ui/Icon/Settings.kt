package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Settings: ImageVector
    get() {
        if (_Setting != null) {
            return _Setting!!
        }
        _Setting = ImageVector.Builder(
            name = "Setting",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            materialPath {
                moveTo(7.218f, 15.004f)
                lineToRelative(0f, -4.667f)
                lineToRelative(1.556f, 0f)
                lineToRelative(0f, 1.556f)
                lineToRelative(6.222f, 0f)
                lineToRelative(0f, 1.556f)
                lineToRelative(-6.222f, 0f)
                lineToRelative(0f, 1.556f)
                lineToRelative(-1.556f, 0f)
                close()
                moveTo(0.995f, 13.449f)
                lineToRelative(0f, -1.556f)
                lineToRelative(4.667f, 0f)
                lineToRelative(0f, 1.556f)
                lineTo(0.995f, 13.449f)
                close()
                moveTo(4.106f, 10.338f)
                lineToRelative(0f, -1.556f)
                lineTo(0.995f, 8.782f)
                lineToRelative(0f, -1.556f)
                lineToRelative(3.111f, 0f)
                lineTo(4.106f, 5.671f)
                lineToRelative(1.556f, 0f)
                lineToRelative(0f, 4.667f)
                lineTo(4.106f, 10.338f)
                close()
                moveTo(7.218f, 8.782f)
                lineToRelative(0f, -1.556f)
                lineToRelative(7.778f, 0f)
                lineToRelative(0f, 1.556f)
                lineTo(7.218f, 8.782f)
                close()
                moveTo(10.329f, 5.671f)
                lineTo(10.329f, 1.004f)
                lineToRelative(1.556f, 0f)
                lineToRelative(0f, 1.556f)
                lineToRelative(3.111f, 0f)
                lineToRelative(0f, 1.556f)
                lineToRelative(-3.111f, 0f)
                lineToRelative(0f, 1.556f)
                lineToRelative(-1.556f, 0f)
                close()
                moveTo(0.995f, 4.115f)
                lineTo(0.995f, 2.56f)
                lineToRelative(7.778f, 0f)
                lineToRelative(0f, 1.556f)
                lineTo(0.995f, 4.115f)
                close()
            }
        }.build()
        return _Setting!!
    }

@Suppress("ObjectPropertyName")
private var _Setting: ImageVector? = null
