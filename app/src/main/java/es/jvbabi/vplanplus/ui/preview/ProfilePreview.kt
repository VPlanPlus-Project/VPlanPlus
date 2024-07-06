package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.preview.GroupPreview.classNames
import java.util.UUID

object ProfilePreview {

    fun generateClassProfile(group: Group, vppId: VppId? = null): ClassProfile {
        val name = classNames.random()
        return ClassProfile(
            id = UUID.randomUUID(),
            originalName = name,
            displayName = name,
            calendarType = ProfileCalendarType.NONE,
            calendarId = null,
            defaultLessons = mapOf(),
            vppId = vppId,
            isHomeworkEnabled = true,
            group = group
        )
    }

    fun generateRoomProfile(room: es.jvbabi.vplanplus.domain.model.Room): RoomProfile {
        val name = RoomPreview.generateRoomNames(1)
        return RoomProfile(
            id = UUID.randomUUID(),
            originalName = name.first(),
            displayName = name.first(),
            calendarType = ProfileCalendarType.NONE,
            calendarId = null,
            room = room
        )
    }
}