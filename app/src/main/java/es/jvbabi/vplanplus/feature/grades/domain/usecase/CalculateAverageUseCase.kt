package es.jvbabi.vplanplus.feature.grades.domain.usecase

import es.jvbabi.vplanplus.feature.grades.domain.model.Grade

class CalculateAverageUseCase {
    operator fun invoke(grades: List<Grade>): Double {
        val avg = mutableListOf<Double>()
        grades.groupBy { it.subject }.entries.sortedBy { it.key.name }.forEach { (_, gradesForSubject) ->
            avg.add(
                gradesForSubject
                    .groupBy { g -> g.type }
                    .map { (_, gradesForType) ->
                        gradesForType.sumOf { grade -> grade.value.toDouble() } / gradesForType.size
                    }
                    .sum() / gradesForSubject.groupBy { g -> g.type }.size
            )
        }
        return avg.average()
    }
}