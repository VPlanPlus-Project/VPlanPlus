package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTaskDone
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile

data class CHomeworkTaskDone(
    @Embedded val taskDone: DbHomeworkTaskDone,
    @Relation(
        parentColumn = "profile_id",
        entityColumn = "id",
        entity = DbClassProfile::class
    ) val profile: CClassProfile
)