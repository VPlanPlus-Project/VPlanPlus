package es.jvbabi.vplanplus.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.exam.DbExamReminder
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import es.jvbabi.vplanplus.domain.model.AssessmentReminder

data class CExamReminder(
    @Embedded val examReminder: DbExamReminder,
    @Relation(
        parentColumn = "profile_id",
        entityColumn = "id",
        entity = DbClassProfile::class
    ) val profile: CClassProfile
) {
    fun toModel() = AssessmentReminder(
        daysBefore = examReminder.daysBefore,
        hasDismissed = examReminder.hasDismissed,
        profile = profile.toModel()
    )
}