package es.jvbabi.vplanplus.feature.main_homework.add.ui.components.due_to

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
@Preview(showBackground = true)
fun DueToModalTitle() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) text@{
        Text(
            text = stringResource(id = R.string.addHomework_until),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}