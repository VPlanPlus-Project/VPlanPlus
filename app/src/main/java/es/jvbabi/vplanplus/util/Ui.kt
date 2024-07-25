package es.jvbabi.vplanplus.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable

@SuppressLint("ComposableNaming")
@Composable
fun runComposable(block: @Composable () -> Unit) {
    block()
}