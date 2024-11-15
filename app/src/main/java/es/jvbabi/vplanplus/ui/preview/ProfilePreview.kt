package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.Group
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.ui.preview.GroupPreview.classNames
import java.time.ZonedDateTime
import java.util.UUID

object ProfilePreview {

    fun generateClassProfile(
        group: Group,
        vppId: VppId.ActiveVppId? = null,
        isDailyNotificationEnabled: Boolean = true,
        isAssessmentsEnabled: Boolean = true
    ): ClassProfile {
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
            isDailyNotificationEnabled = isDailyNotificationEnabled,
            isAssessmentsEnabled = isAssessmentsEnabled,
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

    @PreviewFunction
    fun VppId.toActiveVppId() = VppId.ActiveVppId(
        id = id,
        name = name,
        group = group,
        school = school,
        schoolId = schoolId,
        groupName = groupName,
        email = email,
        state = State.ACTIVE,
        vppIdToken = "",
        schulverwalterToken = "",
        cachedAt = ZonedDateTime.now()
    )
}