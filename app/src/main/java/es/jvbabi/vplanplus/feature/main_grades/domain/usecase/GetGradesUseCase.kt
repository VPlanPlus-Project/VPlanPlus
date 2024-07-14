package es.jvbabi.vplanplus.feature.main_grades.domain.usecase

import es.jvbabi.vplanplus.domain.model.ClassProfile
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetGradesUseCase(
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
    private val gradeRepository: GradeRepository,
) {

    operator fun invoke(): Flow<List<Grade>> = flow {
        getCurrentProfileUseCase().collect { profile ->
            val vppId = (profile as? ClassProfile)?.vppId ?: run { emit(emptyList()); return@collect }
            gradeRepository.getGradesByUser(vppId).collect grades@{ grades -> emit(grades) }
        }
    }
}