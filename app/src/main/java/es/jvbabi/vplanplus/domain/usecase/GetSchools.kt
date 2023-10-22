package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.repository.SchoolRepository

class GetSchools(
    private val schoolRepository: SchoolRepository
) {
    operator fun invoke() = schoolRepository.getSchools()
}