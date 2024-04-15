package es.jvbabi.vplanplus.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

fun Color.toBlackAndWhite(): Color {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(this.toArgb(), hsl)
    hsl[1] = 0f
    return Color(ColorUtils.HSLToColor(hsl))
}

fun blendColor(color1: Color, color2: Color, factor: Float): Color {
    return Color(ColorUtils.blendARGB(color1.toArgb(), color2.toArgb(), factor))
}