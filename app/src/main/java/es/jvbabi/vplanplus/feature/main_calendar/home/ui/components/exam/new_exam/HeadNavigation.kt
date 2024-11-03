package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.RowVerticalCenterSpaceBetweenFill

@Composable
fun HeadNavigation(
    canSave: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    RowVerticalCenterSpaceBetweenFill(Modifier.padding(start = 8.dp, end = 16.dp, bottom = 8.dp)) {
        IconButton(onBack) {
            Icon(Icons.Default.Close, contentDescription = stringResource(android.R.string.cancel))
        }
        AnimatedContent(
            targetState = canSave,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "save button"
        ) { canSave ->
            Button(onSave, enabled = canSave) {
                Text(stringResource(R.string.examsNew_save))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun HeadNavigationPreview() {
    HeadNavigation(true, {}, {})
}