package es.jvbabi.vplanplus.feature.grades.data.repository

import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.grades.data.source.database.GradeDao
import es.jvbabi.vplanplus.feature.grades.data.source.database.SubjectDao
import es.jvbabi.vplanplus.feature.grades.data.source.database.TeacherDao
import es.jvbabi.vplanplus.feature.grades.domain.model.Grade
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.shared.data.TokenAuthentication
import es.jvbabi.vplanplus.shared.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class GradeRepositoryImpl(
    private val teacherDao: TeacherDao,
    private val subjectDao: SubjectDao,
    private val gradeDao: GradeDao,
    private val bsNetworkRepository: NetworkRepository,
    private val vppIdRepository: VppIdRepository
) : GradeRepository {
    override suspend fun updateGrades() {
        val vppIds = vppIdRepository.getVppIds().first()
        vppIds.forEach vppId@{ vppId ->
            val bsToken = vppIdRepository.getBsToken(vppId) ?: return@vppId

            bsNetworkRepository.authentication = TokenAuthentication("Bearer ", bsToken)
        }
    }

    override fun getAllGrades(): Flow<List<Grade>> = flow {
        gradeDao.getAllGrades().collect {
            emit(it.map { g -> g.toModel() })
        }
    }
}