package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun AddExamDetailsSection(
    currentDetails: String,
    onDetailsUpdated: (details: String) -> Unit
) {
    AddExamItem(
        icon = {
            AnimatedContent(
                targetState = currentDetails.isEmpty(),
                label = "details icon"
            ) { isEmpty ->
                if (isEmpty) Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                ) else Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp)
                .padding(start = 8.dp)
                .fillMaxWidth(),
        ) {
            if (currentDetails.isEmpty()) Text(
                modifier = Modifier.padding(top = 12.dp),
                text = stringResource(R.string.examsNew_details_placeholder),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            BasicTextField(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                value = currentDetails,
                onValueChange = onDetailsUpdated,
                minLines = 3,
                textStyle = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}