package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

fun Modifier.grayScale(saturation: Float = 0f): Modifier {
    val saturationMatrix = ColorMatrix().apply { setToSaturation(saturation) }
    val saturationFilter = ColorFilter.colorMatrix(saturationMatrix)
    val paint = Paint().apply { colorFilter = saturationFilter }
    return this.then(
        Modifier.drawWithCache {
            val canvasBounds = Rect(Offset.Zero, size)
            onDrawWithContent {
                drawIntoCanvas {
                    it.saveLayer(canvasBounds, paint)
                    drawContent()
                    it.restore()
                }
            }
        }
    )
}

inline fun Modifier.noRippleClickable(
    crossinline onClick: () -> Unit
): Modifier = composed {
    then(Modifier.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    })
}