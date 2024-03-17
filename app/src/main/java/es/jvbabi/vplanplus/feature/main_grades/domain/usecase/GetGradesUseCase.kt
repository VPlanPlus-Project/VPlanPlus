package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetGradesUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val gradeRepository: GradeRepository,
    private val calculateAverageUseCase: CalculateAverageUseCase
) {

    operator fun invoke(): Flow<GradeState> = flow {
        getCurrentIdentityUseCase().collect { identity ->
            if (identity?.vppId == null) {
                emit(GradeState(emptyList(), 0.0))
                return@collect
            }

            gradeRepository.getGradesByUser(identity.vppId).collect grades@{ grades ->
                emit(GradeState(grades, calculateAverageUseCase(grades)))
            }
        }
    }
}

data class GradeState(
    val grades: List<Grade>,
    val avg: Double
)