package es.jvbabi.vplanplus.feature.main_grades.data.source.example

import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.model.GradeModifier
import java.time.LocalDate
import kotlin.random.Random

class GradesExample(
    private val vppId: VppId
) {

    private val mapping = SubjectsExample.subjects.associateWith { TeacherExample.teachers.random() }

    fun grades(): MutableList<Grade> {
        val grades = mutableListOf<Grade>()
        for (i in 1..10) {
            val m = mapping.entries.random()
            grades.add(
                Grade(
                    id = i.toLong(),
                    vppId = vppId,
                    givenAt = LocalDate.now(),
                    subject = m.key,
                    givenBy = m.value,
                    value = Random.nextInt(1, 6).toFloat(),
                    modifier = GradeModifier.entries[Random.nextInt(0, GradeModifier.entries.size)],
                    type = "KA",
                    comment = "Test KA",
                    interval = ExampleInterval.interval2(false),
                    year = ExampleYear.exampleYear()
                )
            )
        }
        return grades
    }
}