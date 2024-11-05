package es.jvbabi.vplanplus.util

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@SuppressLint("ComposableNaming")
@Composable
fun runComposable(block: @Composable () -> Unit) {
    block()
}

@Composable
fun TextUnit.toDp(): Dp {
    val value = this
    return LocalDensity.current.run { value.toDp() }
}

data class Size(
    val width: Dp,
    val height: Dp
) {
    fun withPadding(padding: Dp): Size {
        return Size(width = width + padding, height = height + padding)
    }

    fun withMinSize(size: Dp): Size {
        return Size(width = width.coerceAtLeast(size), height = height.coerceAtLeast(size))
    }
}

fun Modifier.size(size: Size): Modifier = this.then(Modifier.width(size.width).height(size.height))