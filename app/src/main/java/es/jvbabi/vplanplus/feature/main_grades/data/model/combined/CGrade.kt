package es.jvbabi.vplanplus.feature.main_grades.data.model.combined

import androidx.room.Embedded
import androidx.room.Relation
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.combined.CVppId
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbGrade
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbSubject
import es.jvbabi.vplanplus.feature.main_grades.data.model.DbTeacher
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade

data class CGrade(
    @Embedded val grade: DbGrade,
    @Relation(
        parentColumn = "givenBy",
        entityColumn = "id",
        entity = DbTeacher::class
    ) val teacher: DbTeacher,
    @Relation(
        parentColumn = "subject",
        entityColumn = "id",
        entity = DbSubject::class
    ) val subject: DbSubject,
    @Relation(
        parentColumn = "vppId",
        entityColumn = "id",
        entity = DbVppId::class
    ) val vppId: CVppId
) {
    fun toModel(): Grade {
        return Grade(
            id = grade.id,
            givenAt = grade.givenAt,
            givenBy = teacher.toModel(),
            subject = subject.toModel(),
            modifier = grade.modifier,
            value = grade.value,
            vppId = vppId.toModel(),
            type = grade.type,
            comment = grade.comment
        )
    }
}