package top.k88936.nextcloud_tv.ui.Icon.filetypes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Folder: ImageVector
    get() {
        if (_Folder != null) {
            return _Folder!!
        }
        _Folder = ImageVector.Builder(
            name = "Folder",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            materialPath {
                moveTo(2.4f, 13.6f)
                curveToRelative(-0.385f, 0f, -0.715f, -0.137f, -0.988f, -0.411f)
                arcTo(1.349f, 1.349f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 12.2f)
                lineTo(1f, 3.8f)
                curveToRelative(0f, -0.385f, 0.137f, -0.715f, 0.412f, -0.989f)
                arcTo(1.348f, 1.348f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.4f, 2.4f)
                lineToRelative(4.2f, 0f)
                lineToRelative(1.4f, 1.4f)
                lineToRelative(5.6f, 0f)
                curveToRelative(0.385f, 0f, 0.715f, 0.137f, 0.989f, 0.411f)
                curveToRelative(0.274f, 0.274f, 0.411f, 0.604f, 0.411f, 0.989f)
                lineToRelative(0f, 7f)
                arcToRelative(
                    1.351f,
                    1.351f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.411f,
                    0.989f
                )
                arcTo(
                    1.351f,
                    1.351f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    13.6f,
                    13.6f
                )
                lineTo(2.4f, 13.6f)
                close()
            }
        }.build()

        return _Folder!!
    }

@Suppress("ObjectPropertyName")
private var _Folder: ImageVector? = null
