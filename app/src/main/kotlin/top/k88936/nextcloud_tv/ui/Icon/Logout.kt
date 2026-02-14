package androidx.compose.material.icons.filled

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Filled.Logout: ImageVector
    get() {
        if (_Logout != null) {
            return _Logout!!
        }
        _Logout = ImageVector.Builder(
            name = "Logout",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(2.556f, 15f)
                curveToRelative(-0.428f, 0f, -0.794f, -0.152f, -1.098f, -0.457f)
                arcTo(1.499f, 1.499f, 45f, isMoreThanHalf = false, isPositiveArc = true, 1f, 13.445f)
                lineTo(1f, 2.556f)
                curveToRelative(0f, -0.428f, 0.152f, -0.794f, 0.457f, -1.099f)
                arcTo(1.498f, 1.498f, 135f, isMoreThanHalf = false, isPositiveArc = true, 2.556f, 1f)
                lineToRelative(5.444f, 0f)
                lineToRelative(0f, 1.556f)
                lineTo(2.556f, 2.556f)
                lineToRelative(0f, 10.889f)
                lineToRelative(5.444f, 0f)
                lineToRelative(0f, 1.556f)
                lineTo(2.556f, 15f)
                close()
                moveTo(11.111f, 11.889f)
                lineTo(10.042f, 10.761f)
                lineTo(12.025f, 8.778f)
                lineTo(5.667f, 8.778f)
                lineToRelative(0f, -1.556f)
                lineToRelative(6.358f, 0f)
                lineToRelative(-1.983f, -1.983f)
                lineTo(11.111f, 4.111f)
                lineToRelative(3.889f, 3.889f)
                lineToRelative(-3.889f, 3.889f)
                close()
            }
        }.build()

        return _Logout!!
    }

@Suppress("ObjectPropertyName")
private var _Logout: ImageVector? = null
