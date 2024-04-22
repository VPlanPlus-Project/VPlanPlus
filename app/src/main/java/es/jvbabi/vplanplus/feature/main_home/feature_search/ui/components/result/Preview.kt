package es.jvbabi.vplanplus.feature.main_home.feature_search.ui.components.result

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import es.jvbabi.vplanplus.data.model.SchoolEntityType
import java.time.ZonedDateTime

@Preview(showBackground = true)
@Composable
private fun SearchResultPreview() {
    SearchResult(
        es.jvbabi.vplanplus.feature.main_home.feature_search.ui.SearchResult(
            name = "10c",
            type = SchoolEntityType.CLASS,
            school = "Einstein-School",
            lessons = emptyList(),
            bookings = emptyList()
        ),
        ZonedDateTime.now()
    )
}