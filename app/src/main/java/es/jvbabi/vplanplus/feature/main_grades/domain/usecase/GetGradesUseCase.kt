package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetGradesUseCase(
    private val getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    private val gradeRepository: GradeRepository,
) {

    operator fun invoke(): Flow<List<Grade>> = flow {
        getCurrentIdentityUseCase().collect { identity ->
            if (identity?.profile?.vppId == null) {
                emit(emptyList())
                return@collect
            }

            gradeRepository.getGradesByUser(identity.profile.vppId).collect grades@{ grades ->
                emit(grades)
            }
        }
    }
}