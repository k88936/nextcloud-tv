package top.k88936.nextcloud_tv.ui.Icon.filetypes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Image: ImageVector
    get() {
        if (_Image != null) {
            return _Image!!
        }
        _Image = ImageVector.Builder(
            name = "Image",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            materialPath {
                moveTo(5f, 21f)
                curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.412f, -0.587f)
                arcTo(1.927f, 1.927f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 19f)
                lineTo(3f, 5f)
                curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.588f, -1.413f)
                arcTo(1.926f, 1.926f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 3f)
                horizontalLineToRelative(14f)
                curveToRelative(0.55f, 0f, 1.021f, 0.196f, 1.413f, 0.587f)
                curveToRelative(0.391f, 0.392f, 0.587f, 0.863f, 0.587f, 1.413f)
                verticalLineToRelative(14f)
                arcToRelative(
                    1.93f,
                    1.93f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.587f,
                    1.413f
                )
                arcTo(1.93f, 1.93f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 21f)
                lineTo(5f, 21f)
                close()
                moveTo(6f, 17f)
                horizontalLineToRelative(12f)
                lineToRelative(-3.75f, -5f)
                lineToRelative(-3f, 4f)
                lineTo(9f, 13f)
                lineToRelative(-3f, 4f)
                close()
                moveTo(8.5f, 10f)
                arcToRelative(
                    1.45f,
                    1.45f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    1.063f,
                    -0.437f
                )
                arcTo(1.45f, 1.45f, 0f, isMoreThanHalf = false, isPositiveArc = false, 10f, 8.5f)
                curveToRelative(0f, -0.417f, -0.146f, -0.771f, -0.437f, -1.062f)
                arcTo(1.447f, 1.447f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8.5f, 7f)
                curveToRelative(-0.417f, 0f, -0.771f, 0.146f, -1.062f, 0.438f)
                arcTo(1.443f, 1.443f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7f, 8.5f)
                curveToRelative(0f, 0.417f, 0.146f, 0.771f, 0.438f, 1.063f)
                curveToRelative(0.291f, 0.291f, 0.645f, 0.437f, 1.062f, 0.437f)
                close()
            }
        }.build()

        return _Image!!
    }

@Suppress("ObjectPropertyName")
private var _Image: ImageVector? = null
