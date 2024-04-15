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
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.RoomBooking
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchResult
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result.components.SearchResultBooking
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

        val records: List<Any> = ((searchResult.lessons ?: emptyList()) + searchResult.bookings)
            .sortedBy { (it as? Lesson)?.start?.toInstant()?.epochSecond ?: (it as? RoomBooking)?.from?.toInstant()?.epochSecond }
        LazyRow(modifier = Modifier.padding(top = 8.dp)) {
            item { Spacer(modifier = Modifier.width(8.dp)) }
            items(records) { record ->
                if (record is Lesson) SearchResultLesson(lesson = record, resultType = searchResult.type, currentTime = time)
                else if (record is RoomBooking) SearchResultBooking(booking = record, resultType = searchResult.type, currentTime = time)
            }
        }
    }
}