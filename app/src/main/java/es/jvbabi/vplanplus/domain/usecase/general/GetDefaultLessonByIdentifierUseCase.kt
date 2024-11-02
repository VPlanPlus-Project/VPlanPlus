package es.jvbabi.vplanplus.domain.usecase.general

import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository

class GetDefaultLessonByIdentifierUseCase(
    private val schoolRepository: SchoolRepository,
    private val defaultLessonRepository: DefaultLessonRepository
) {
    companion object {
        private const val KEY_SP24SCHOOL_ID = "sp24school"
        private const val KEY_SP24VP_ID = "sp24vp"

    }
    suspend operator fun invoke(identifier: String): DefaultLesson? {
        val sp24MatchResult = Regex("sp24\\.(?<$KEY_SP24SCHOOL_ID>\\d{8})\\.(?<$KEY_SP24VP_ID>\\d+)").find(identifier)
        if (sp24MatchResult != null) {
            val sp24SchoolId = sp24MatchResult.groups[KEY_SP24SCHOOL_ID]!!.value.toInt()
            val school = schoolRepository.getSchoolBySp24Id(sp24SchoolId) ?: return null

            val vpId = sp24MatchResult.groups[KEY_SP24VP_ID]!!.value.toInt()
            return defaultLessonRepository.getDefaultLessonsBySchool(school).find { it.vpId == vpId }
        }
        TODO("Not implemented")
    }
}