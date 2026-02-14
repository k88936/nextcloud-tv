package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

public val Icons.Filled.Photos: ImageVector
    get() {
        if (_photos != null) {
            return _photos!!
        }
        _photos = ImageVector.Builder(
            name = "Photos",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 32f,
            viewportHeight = 32f
        ).apply {
            materialPath {
                moveTo(5.111f, 30f)
                curveToRelative(-0.856f, 0f, -1.588f, -0.305f, -2.196f, -0.913f)
                arcTo(
                    2.998f,
                    2.998f,
                    45f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    2f,
                    26.889f
                )
                lineTo(2f, 5.111f)
                curveToRelative(0f, -0.856f, 0.305f, -1.588f, 0.915f, -2.198f)
                arcTo(
                    2.996f,
                    2.996f,
                    135f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    5.111f,
                    2f
                )
                lineToRelative(21.778f, 0f)
                curveToRelative(0.856f, 0f, 1.588f, 0.305f, 2.198f, 0.913f)
                curveToRelative(0.608f, 0.61f, 0.913f, 1.342f, 0.913f, 2.198f)
                lineToRelative(0f, 21.778f)
                arcToRelative(
                    3.002f,
                    3.002f,
                    45f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    -0.913f,
                    2.198f
                )
                arcTo(
                    3.002f,
                    3.002f,
                    135f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    26.889f,
                    30f
                )
                lineTo(5.111f, 30f)
                close()
                moveTo(6.666f, 23.778f)
                lineToRelative(18.667f, 0f)
                lineToRelative(-5.833f, -7.778f)
                lineToRelative(-4.667f, 6.222f)
                lineTo(11.333f, 17.555f)
                lineToRelative(-4.667f, 6.222f)
                close()
                moveTo(10.555f, 12.889f)
                arcToRelative(
                    2.256f,
                    2.256f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    1.654f,
                    -0.68f
                )
                arcTo(
                    2.256f,
                    2.256f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    12.889f,
                    10.555f
                )
                curveToRelative(0f, -0.649f, -0.227f, -1.199f, -0.68f, -1.652f)
                arcTo(
                    2.251f,
                    2.251f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    10.555f,
                    8.222f
                )
                curveToRelative(-0.649f, 0f, -1.199f, 0.227f, -1.652f, 0.681f)
                arcTo(
                    2.245f,
                    2.245f,
                    0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    8.222f,
                    10.555f
                )
                curveToRelative(0f, 0.649f, 0.227f, 1.199f, 0.681f, 1.654f)
                curveToRelative(0.453f, 0.453f, 1.003f, 0.68f, 1.652f, 0.68f)
                close()
            }
        }.build()

        return _photos!!
    }

private var _photos: ImageVector? = null
