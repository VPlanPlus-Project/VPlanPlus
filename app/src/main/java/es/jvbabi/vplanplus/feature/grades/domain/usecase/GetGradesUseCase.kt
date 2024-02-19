package es.jvbabi.vplanplus.feature.grades.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetGradesUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val gradeRepository: GradeRepository
) {

    operator fun invoke(): Flow<GradeState> = flow {
        getCurrentIdentityUseCase().collect { identity ->
            if (identity?.vppId == null) {
                emit(GradeState(emptyList(), 0.0))
                return@collect
            }

            gradeRepository.getGradesByUser(identity.vppId).collect grades@{ grades ->
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
                emit(GradeState(grades, avg.average()))
            }
        }
    }
}

data class GradeState(
    val grades: List<Grade>,
    val avg: Double
)