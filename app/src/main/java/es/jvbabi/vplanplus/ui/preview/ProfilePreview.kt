package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.data.model.ProfileCalendarType
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.preview.ClassesPreview.classNames
import java.util.UUID

object ProfilePreview {

    fun generateClassProfile(vppId: VppId? = null): Profile {
        val name = classNames.random()
        return Profile(
            id = UUID.randomUUID(),
            originalName = name,
            displayName = name,
            type = ProfileType.STUDENT,
            referenceId = UUID.randomUUID(),
            calendarType = ProfileCalendarType.NONE,
            calendarId = null,
            defaultLessons = mapOf(),
            vppId = vppId,
            isHomeworkEnabled = true
        )
    }

    fun generateRoomProfile(): Profile {
        val name = Room.generateRoomNames(1)
        return Profile(
            id = UUID.randomUUID(),
            originalName = name.first(),
            displayName = name.first(),
            type = ProfileType.ROOM,
            referenceId = UUID.randomUUID(),
            calendarType = ProfileCalendarType.NONE,
            calendarId = null,
            defaultLessons = mapOf(),
            vppId = null,
            isHomeworkEnabled = false
        )
    }
}