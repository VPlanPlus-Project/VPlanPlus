package es.jvbabi.vplanplus.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
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