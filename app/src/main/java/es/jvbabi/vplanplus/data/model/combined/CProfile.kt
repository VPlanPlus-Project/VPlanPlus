package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile

data class CProfile(
    @Embedded val profile: DbProfile,
    @Relation(
        parentColumn = "id",
        entityColumn = "profileId",
        entity = DbProfileDefaultLesson::class
    )
    val defaultLessons: List<CProfileDefaultLesson>
) {
    fun toModel(): Profile {
        return Profile(
            id = profile.id!!,
            displayName = profile.customName,
            defaultLessons = mapOf(),
            type = profile.type,
            referenceId = profile.referenceId,
            calendarType = profile.calendarMode,
            calendarId = profile.calendarId,
            originalName = profile.name,
        )
    }
}