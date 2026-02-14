package androidx.compose.material.icons.filled

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

val Icons.Filled.Files: ImageVector
    get() {
        if (_files != null) {
            return _files!!
        }
        _files = ImageVector.Builder(
            name = "Files",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            materialPath {
                moveTo(168f, 768f)
                quadToRelative(-29f, 0f, -50.5f, -21.5f)
                reflectiveQuadTo(96f, 696f)
                verticalLineToRelative(-432f)
                quadToRelative(0f, -30f, 21.5f, -51f)
                reflectiveQuadToRelative(50.5f, -21f)
                horizontalLineToRelative(216f)
                lineToRelative(96f, 96f)
                horizontalLineToRelative(312f)
                quadToRelative(30f, 0f, 51f, 21f)
                reflectiveQuadToRelative(21f, 51f)
                verticalLineToRelative(336f)
                quadToRelative(0f, 29f, -21f, 50.5f)
                reflectiveQuadTo(792f, 768f)
                lineTo(168f, 768f)
                close()
            }
        }.build()
        return _files!!
    }

private var _files: ImageVector? = null
