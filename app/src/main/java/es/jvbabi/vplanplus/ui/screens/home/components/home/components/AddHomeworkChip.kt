package es.jvbabi.vplanplus.ui.screens.home.components.home.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun AddHomeworkChip(
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(text = stringResource(id = R.string.home_addHomeworkLabel)) },
        modifier = Modifier.padding(8.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.MenuBook,
                contentDescription = null
            )
        }
    )
}

@Composable
@Preview(showBackground = true)
fun AddHomeworkChipPreview() {
    AddHomeworkChip(onClick = {})
}