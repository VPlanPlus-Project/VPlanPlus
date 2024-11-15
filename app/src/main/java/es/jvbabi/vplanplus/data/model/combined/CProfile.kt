package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.vppid.DbVppId
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import es.jvbabi.vplanplus.data.model.profile.DbRoomProfile
import es.jvbabi.vplanplus.data.model.profile.DbTeacherProfile
import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.model.RoomProfile
import es.jvbabi.vplanplus.domain.model.TeacherProfile
import es.jvbabi.vplanplus.domain.model.VppId

data class CTeacherProfile(
    @Embedded val teacherProfile: DbTeacherProfile,
    @Relation(
        parentColumn = "teacher_id",
        entityColumn = "id",
        entity = DbSchoolEntity::class
    ) val schoolEntity: CSchoolEntity
) {
    fun toModel(): TeacherProfile {
        return TeacherProfile(
            id = teacherProfile.id,
            originalName = teacherProfile.name,
            displayName = teacherProfile.customName,
            calendarType = teacherProfile.calendarMode,
            calendarId = teacherProfile.calendarId,
            teacher = schoolEntity.toTeacherModel()
        )
    }
}

data class CRoomProfile(
    @Embedded val roomProfile: DbRoomProfile,
    @Relation(
        parentColumn = "room_id",
        entityColumn = "id",
        entity = DbRoom::class
    ) val room: CRoom
) {
    fun toModel(): RoomProfile {
        return RoomProfile(
            id = roomProfile.id,
            originalName = roomProfile.name,
            displayName = roomProfile.customName,
            calendarType = roomProfile.calendarMode,
            calendarId = roomProfile.calendarId,
            room = room.toModel()
        )
    }
}

data class CClassProfile(
    @Embedded val classProfile: DbClassProfile,
    @Relation(
        parentColumn = "class_id",
        entityColumn = "id",
        entity = DbGroup::class
    ) val group: CGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "profile_id",
        entity = DbProfileDefaultLesson::class
    )
    val defaultLessons: List<CProfileDefaultLesson>,
    @Relation(
        parentColumn = "vpp_id",
        entityColumn = "id",
        entity = DbVppId::class
    ) val vppId: CVppId?
) {
    fun toModel(): ClassProfile {
        return ClassProfile(
            id = classProfile.id,
            originalName = classProfile.name,
            displayName = classProfile.customName,
            calendarType = classProfile.calendarMode,
            calendarId = classProfile.calendarId,
            group = group.toModel(),
            isHomeworkEnabled = classProfile.isHomeworkEnabled,
            isAssessmentsEnabled = classProfile.isAssessmentsEnabled,
            isDailyNotificationEnabled = classProfile.isDailyNotificationEnabled,
            defaultLessons = defaultLessons
                .mapNotNull { defaultLesson ->
                    val defaultLessonModel = defaultLesson.defaultLessons.firstOrNull { it.`class`.school.school.id == group.school.school.id && it.defaultLesson.vpId.toLong() == defaultLesson.profileDefaultLesson.defaultLessonVpId }?.toModel() ?: return@mapNotNull null
                    defaultLessonModel to defaultLesson.profileDefaultLesson.enabled
                }
                .toMap(),
            vppId = vppId?.toModel() as? VppId.ActiveVppId
        )
    }
}