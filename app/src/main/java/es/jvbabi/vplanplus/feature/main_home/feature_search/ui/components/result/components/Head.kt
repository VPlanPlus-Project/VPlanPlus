package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.ui.common.DOT

@Composable
fun SearchResultHead(
    name: String,
    school: String,
    lessons: Int?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(text = name, style = MaterialTheme.typography.headlineSmall)
        if (lessons == null) return@Row
        Text(
            text = DOT,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        if (lessons == 0) Text(
            text = stringResource(id = R.string.search_noLessons),
            style = MaterialTheme.typography.bodyMedium
        )
        else Text(
            text = stringResource(
                id = R.string.search_lessons,
                lessons
            ), style = MaterialTheme.typography.bodyMedium
        )
    }
    Text(
        text = school,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier.padding(start = 8.dp)
    )
}