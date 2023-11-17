package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.source.database.crossover.ProfileSelectedDefaultLessonCrossover
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Profile

data class CProfile(
    @Embedded val profile: DbProfile,
    @Relation(
        parentColumn = "id",
        entityColumn = "defaultLessonId",
        associateBy = Junction(
            parentColumn = "psdlcProfileId",
            entityColumn = "psdlcDefaultLessonId",
            value = ProfileSelectedDefaultLessonCrossover::class,
        ),
        entity = DefaultLesson::class
    ) val enabledDefaultLessons: List<DefaultLesson>
) {
    fun toModel(): Profile {
        return Profile(
            id = profile.id!!,
            displayName = profile.customName,
            allowedLessons = enabledDefaultLessons,
            type = profile.type,
            referenceId = profile.referenceId,
            calendarType = profile.calendarMode,
            calendarId = profile.calendarId,
            originalName = profile.name,
        )
    }
}