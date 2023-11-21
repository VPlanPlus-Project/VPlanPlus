package es.jvbabi.vplanplus.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FullLoadingCircle() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator()
    }
}