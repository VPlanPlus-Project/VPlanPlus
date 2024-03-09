package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.grades.domain.repository.GradeRepository

class GetVppIdDetailsUseCase(
    private val vppIdRepository: VppIdRepository,
    private val classRepository: ClassRepository,
    private val gradeRepository: GradeRepository
) {

    suspend operator fun invoke(token: String): DataResponse<VppId?> {
        val response = vppIdRepository.getVppIdOnline(token)
        if (response.data != null) {
            val `class` = classRepository.getClassBySchoolIdAndClassName(response.data.schoolId, response.data.className)!!
            val vppId = VppId(
                id = response.data.id,
                email = response.data.email,
                name = response.data.username,
                state = State.ACTIVE,
                classes = `class`,
                className = response.data.className,
                school = `class`.school,
                schoolId = response.data.schoolId
            )
            vppIdRepository.addVppId(vppId)
            vppIdRepository.addVppIdToken(vppId, token, response.data.bsToken, true)

            gradeRepository.updateGrades()

            return DataResponse(vppId, response.response)
        }
        return DataResponse(null, response.response)
    }
}