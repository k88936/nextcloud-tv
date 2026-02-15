package top.k88936.nextcloud_tv.ui.Icon.filetypes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.File: ImageVector
    get() {
        if (_File != null) {
            return _File!!
        }
        _File = ImageVector.Builder(
            name = "File",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            materialPath {
                moveTo(3.77f, 15.012f)
                curveToRelative(-0.385f, 0f, -0.715f, -0.137f, -0.988f, -0.411f)
                arcTo(
                    1.349f,
                    1.349f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    2.37f,
                    13.612f
                )
                lineTo(2.37f, 2.412f)
                curveToRelative(0f, -0.385f, 0.137f, -0.715f, 0.412f, -0.989f)
                arcTo(
                    1.348f,
                    1.348f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    3.77f,
                    1.012f
                )
                lineToRelative(5.6f, 0f)
                lineToRelative(4.2f, 4.2f)
                lineToRelative(0f, 8.4f)
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
                    12.17f,
                    15.012f
                )
                lineTo(3.77f, 15.012f)
                close()
            }
        }.build()

        return _File!!
    }

@Suppress("ObjectPropertyName")
private var _File: ImageVector? = null
