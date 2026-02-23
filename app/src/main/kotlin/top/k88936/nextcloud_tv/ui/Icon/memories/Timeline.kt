package top.k88936.nextcloud_tv.ui.Icon.memories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Timeline: ImageVector
    get() {
        if (_Timeline != null) {
            return _Timeline!!
        }
        _Timeline = ImageVector.Builder(
            name = "Timeline",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            materialPath {
                moveTo(22f, 16f)
                verticalLineTo(4f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 20f, 2f)
                horizontalLineTo(8f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 6f, 4f)
                verticalLineTo(16f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8f, 18f)
                horizontalLineTo(20f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 22f, 16f)
                moveTo(11f, 12f)
                lineTo(13.03f, 14.71f)
                lineTo(16f, 11f)
                lineTo(20f, 16f)
                horizontalLineTo(8f)
                moveTo(2f, 6f)
                verticalLineTo(20f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 4f, 22f)
                horizontalLineTo(18f)
                verticalLineTo(20f)
                horizontalLineTo(4f)
                verticalLineTo(6f)
            }
        }.build()

        return _Timeline!!
    }

@Suppress("ObjectPropertyName")
private var _Timeline: ImageVector? = null
