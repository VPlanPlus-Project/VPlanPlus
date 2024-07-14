package es.jvbabi.vplanplus.feature.main_homework.add.ui.components.default_lesson_dialog

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
fun DefaultLessonSheetTitle(hasDefaultLessonsFiltered: Boolean) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) text@{
        Text(
            text = stringResource(id = R.string.addHomework_defaultLessonTitle),
            style = MaterialTheme.typography.bodyLarge
        )
        if(hasDefaultLessonsFiltered) Text(text = stringResource(id = R.string.addHomework_defaultLessonFilteredMessage))
    }
}

@Preview(showBackground = true)
@Composable
private fun TitlePreview() {
    DefaultLessonSheetTitle(hasDefaultLessonsFiltered = false)
}

@Preview(showBackground = true)
@Composable
private fun TitleFilteredPreview() {
    DefaultLessonSheetTitle(hasDefaultLessonsFiltered = true)
}