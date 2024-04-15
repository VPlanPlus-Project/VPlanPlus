package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchResult
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result.components.SearchResultHead
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result.components.SearchResultLesson
import java.time.ZonedDateTime

@Composable
fun SearchResult(
    searchResult: SearchResult,
    time: ZonedDateTime
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
    ) {
        SearchResultHead(
            name = searchResult.name,
            school = searchResult.school,
            lessons = searchResult.lessons?.size
        )
        LazyRow(modifier = Modifier.padding(top = 8.dp)) {
            item { Spacer(modifier = Modifier.width(8.dp)) }
            items(searchResult.lessons ?: emptyList()) { lesson -> SearchResultLesson(lesson = lesson, resultType = searchResult.type, currentTime = time) }
        }
    }
}