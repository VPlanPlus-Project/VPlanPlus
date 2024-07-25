package es.jvbabi.vplanplus.feature.main_grades.view.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.combined.CVppId
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbGrade
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbInterval
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbSubject
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbTeacher
import es.jvbabi.vplanplus.feature.main_grades.view.domain.model.Grade

data class CGrade(
    @Embedded val grade: DbGrade,
    @Relation(
        parentColumn = "teacher_id",
        entityColumn = "id",
        entity = DbTeacher::class
    ) val teacher: DbTeacher,
    @Relation(
        parentColumn = "subject_id",
        entityColumn = "id",
        entity = DbSubject::class
    ) val subject: DbSubject,
    @Relation(
        parentColumn = "vpp_id",
        entityColumn = "id",
        entity = DbVppId::class
    ) val vppId: CVppId,
    @Relation(
        parentColumn = "interval_id",
        entityColumn = "id",
        entity = DbInterval::class
    ) val interval: CInterval
) {
    fun toModel(): Grade {
        val (interval, year) = interval.toModel()
        return Grade(
            id = grade.id,
            givenAt = grade.givenAt,
            givenBy = teacher.toModel(),
            subject = subject.toModel(),
            modifier = grade.modifier,
            value = grade.value,
            vppId = vppId.toModel(),
            type = grade.type,
            comment = grade.comment,
            interval = interval,
            year = year
        )
    }
}