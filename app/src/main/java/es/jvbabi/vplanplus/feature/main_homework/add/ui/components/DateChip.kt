package es.jvbabi.vplanplus.feature.main_homework.add.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.util.DateUtils
import java.time.LocalDate

@Composable
fun DateChip(
    date: LocalDate,
    selected: Boolean,
    onClicked: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    FilterChip(
        label = { Text(text = DateUtils.localizedRelativeDate(context, date))},
        onClick = { onClicked(date) },
        modifier = Modifier.padding(horizontal = 4.dp),
        selected = selected
    )
}