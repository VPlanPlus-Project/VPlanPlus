package es.jvbabi.vplanplus.feature.ndp.ui.guided

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import es.jvbabi.vplanplus.domain.model.Lesson
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun NdpLessonsScreen(
    lessons: List<Lesson>,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text("Subjects for next school day")
        Text("Start: " + lessons.minOf { it.start }.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
        lessons.distinctBy { it.subject }.forEach { lessons ->
            Text(lessons.subject)
        }
        Text("End: " + lessons.maxOf { it.start }.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)))
        Button(
            onClick = onContinue
        ) {
            Text("Ok")
        }
    }
}