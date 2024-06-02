package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.ProfileType
import es.jvbabi.vplanplus.domain.model.Profile

data class CProfile(
    @Embedded val profile: DbProfile,
    @Relation(
        parentColumn = "profileId",
        entityColumn = "profileId",
        entity = DbProfileDefaultLesson::class
    )
    val defaultLessons: List<CProfileDefaultLesson>,
    @Relation(
        parentColumn = "linkedVppId",
        entityColumn = "id",
        entity = DbVppId::class
    ) val vppId: CVppId?
) {
    fun toModel(): Profile {
        return Profile(
            id = profile.profileId,
            displayName = profile.customName,
            defaultLessons = defaultLessons.associate {
                val defaultLesson = it.defaultLessons.first { dl ->
                    (profile.type == ProfileType.STUDENT && dl.defaultLesson.classId == profile.referenceId) ||
                            (profile.type == ProfileType.TEACHER && dl.defaultLesson.teacherId == profile.referenceId)
                }
                defaultLesson.toModel() to it.profileDefaultLesson.enabled
            },
            type = profile.type,
            referenceId = profile.referenceId,
            calendarType = profile.calendarMode,
            calendarId = profile.calendarId,
            originalName = profile.name,
            vppId = vppId?.toModel(),
            isHomeworkEnabled = profile.isHomeworkEnabled
        )
    }
}