package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile

object Profile {

    fun generateClassProfile(): Profile {
        return Profile(
            id = 1,
            originalName = "7c",
            displayName = "7c",
            type = ProfileType.STUDENT,
            referenceId = 1,
            calendarType = ProfileCalendarType.NONE,
            calendarId = null,
            allowedLessons = listOf()
        )
    }
}