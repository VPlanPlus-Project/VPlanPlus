package es.jvbabi.vplanplus.domain.usecase.vpp_id

import es.jvbabi.vplanplus.domain.DataResponse
import es.jvbabi.vplanplus.domain.model.State
import es.jvbabi.vplanplus.domain.model.VppId
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import java.time.ZonedDateTime

class GetVppIdDetailsUseCase(
    private val vppIdRepository: VppIdRepository,
    private val groupRepository: GroupRepository,
    private val gradeRepository: GradeRepository
) {

    suspend operator fun invoke(token: String): DataResponse<VppId?> {
        val response = vppIdRepository.getVppIdOnline(token)
        if (response.data != null) {
            val `class` = groupRepository.getGroupBySchoolAndName(response.data.schoolId, response.data.className)
            val vppId = VppId(
                id = response.data.id,
                email = response.data.email,
                name = response.data.username,
                state = State.ACTIVE,
                group = `class`,
                groupName = response.data.className,
                school = `class`?.school,
                schoolId = response.data.schoolId,
                cachedAt = ZonedDateTime.now()
            )
            if (vppId.group != null) {
                vppIdRepository.addVppId(vppId)
                vppIdRepository.addVppIdToken(vppId, token, response.data.bsToken, true)
                gradeRepository.updateGrades()
            }

            return DataResponse(vppId, response.response)
        }
        return DataResponse(null, response.response)
    }
}