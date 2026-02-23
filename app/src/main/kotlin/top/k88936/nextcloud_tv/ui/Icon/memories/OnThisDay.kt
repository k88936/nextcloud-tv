package top.k88936.nextcloud_tv.ui.Icon.memories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.OnThisDay: ImageVector
    get() {
        if (_OnThisDay != null) {
            return _OnThisDay!!
        }
        _OnThisDay = ImageVector.Builder(
            name = "OnThisDay",
            defaultWidth = 20.dp,
            defaultHeight = 20.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            materialPath {
                moveTo(19f, 19f)
                horizontalLineTo(5f)
                verticalLineTo(8f)
                horizontalLineTo(19f)
                moveTo(16f, 1f)
                verticalLineTo(3f)
                horizontalLineTo(8f)
                verticalLineTo(1f)
                horizontalLineTo(6f)
                verticalLineTo(3f)
                horizontalLineTo(5f)
                curveTo(3.89f, 3f, 3f, 3.89f, 3f, 5f)
                verticalLineTo(19f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 5f, 21f)
                horizontalLineTo(19f)
                arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 21f, 19f)
                verticalLineTo(5f)
                curveTo(21f, 3.89f, 20.1f, 3f, 19f, 3f)
                horizontalLineTo(18f)
                verticalLineTo(1f)
                moveTo(17f, 12f)
                horizontalLineTo(12f)
                verticalLineTo(17f)
                horizontalLineTo(17f)
                verticalLineTo(12f)
                close()
            }
        }.build()

        return _OnThisDay!!
    }

@Suppress("ObjectPropertyName")
private var _OnThisDay: ImageVector? = null
