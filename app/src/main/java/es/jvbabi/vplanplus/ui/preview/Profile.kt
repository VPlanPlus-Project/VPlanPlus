package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import java.util.UUID

object Profile {

    fun generateClassProfile(): Profile {
        return Profile(
            id = UUID.randomUUID(),
            originalName = "7c",
            displayName = "7c",
            type = ProfileType.STUDENT,
            referenceId = UUID.randomUUID(),
            calendarType = ProfileCalendarType.NONE,
            calendarId = null,
            defaultLessons = mapOf()
        )
    }
}