package top.k88936.nextcloud_tv.ui.Icon.filetypes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Pdf: ImageVector
    get() {
        if (_Pdf != null) {
            return _Pdf!!
        }
        _Pdf = ImageVector.Builder(
            name = "Pdf",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            materialPath {
                moveTo(19f, 3f)
                horizontalLineTo(5f)
                curveTo(3.9f, 3f, 3f, 3.9f, 3f, 5f)
                verticalLineTo(19f)
                curveTo(3f, 20.1f, 3.9f, 21f, 5f, 21f)
                horizontalLineTo(19f)
                curveTo(20.1f, 21f, 21f, 20.1f, 21f, 19f)
                verticalLineTo(5f)
                curveTo(21f, 3.9f, 20.1f, 3f, 19f, 3f)
                close()
                moveTo(9.5f, 11.5f)
                curveTo(9.5f, 12.3f, 8.8f, 13f, 8f, 13f)
                horizontalLineTo(7f)
                verticalLineTo(15f)
                horizontalLineTo(5.5f)
                verticalLineTo(9f)
                horizontalLineTo(8f)
                curveTo(8.8f, 9f, 9.5f, 9.7f, 9.5f, 10.5f)
                verticalLineTo(11.5f)
                close()
                moveTo(14.5f, 13.5f)
                curveTo(14.5f, 14.3f, 13.8f, 15f, 13f, 15f)
                horizontalLineTo(10.5f)
                verticalLineTo(9f)
                horizontalLineTo(13f)
                curveTo(13.8f, 9f, 14.5f, 9.7f, 14.5f, 10.5f)
                verticalLineTo(13.5f)
                close()
                moveTo(18.5f, 10.5f)
                horizontalLineTo(17f)
                verticalLineTo(11.5f)
                horizontalLineTo(18.5f)
                verticalLineTo(13f)
                horizontalLineTo(17f)
                verticalLineTo(15f)
                horizontalLineTo(15.5f)
                verticalLineTo(9f)
                horizontalLineTo(18.5f)
                verticalLineTo(10.5f)
                close()
                moveTo(12f, 10.5f)
                horizontalLineTo(13f)
                verticalLineTo(13.5f)
                horizontalLineTo(12f)
                verticalLineTo(10.5f)
                close()
                moveTo(7f, 10.5f)
                horizontalLineTo(8f)
                verticalLineTo(11.5f)
                horizontalLineTo(7f)
                verticalLineTo(10.5f)
                close()
            }
        }.build()

        return _Pdf!!
    }

@Suppress("ObjectPropertyName")
private var _Pdf: ImageVector? = null
