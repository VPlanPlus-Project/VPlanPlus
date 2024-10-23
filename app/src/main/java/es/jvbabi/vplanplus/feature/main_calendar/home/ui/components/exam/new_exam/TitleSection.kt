package es.jvbabi.vplanplus.feature.main_calendar.home.ui.components.exam.new_exam

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R

@Composable
fun AddExamTitleSection(
    currentTitle: String,
    onUpdateTitle: (title: String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    AddExamItem(
        iconContainerSize = 40.dp
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = currentTitle,
            onValueChange = onUpdateTitle,
            placeholder = { Text(text = stringResource(R.string.examsNew_topic), style = MaterialTheme.typography.headlineMedium, color = Color.Gray) },
            textStyle = MaterialTheme.typography.headlineMedium,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AddExamTitleSectionPreview() {
    AddExamTitleSection("") {}
}

@Preview(showBackground = true)
@Composable
private fun AddExamTitleSectionPreview2() {
    AddExamTitleSection("Test", {})
}