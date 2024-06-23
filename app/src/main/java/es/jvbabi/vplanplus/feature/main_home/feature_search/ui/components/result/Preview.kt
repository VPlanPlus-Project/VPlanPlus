package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchResult
import java.time.ZonedDateTime

@Preview(showBackground = true)
@Composable
private fun SearchResultPreview() {
    SearchResult(
        SearchResult(
            name = "10c",
            type = ProfileType.STUDENT,
            school = "Einstein-School",
            lessons = emptyList(),
            bookings = emptyList()
        ),
        ZonedDateTime.now()
    )
}