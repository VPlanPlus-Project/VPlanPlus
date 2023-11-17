package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.ProfileType
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.ui.screens.home.Day
import es.jvbabi.vplanplus.ui.screens.home.DayType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class HomeUseCases(
    private val lessonRepository: LessonRepository,
) {
    suspend fun getLessons(profile: Profile, date: LocalDate): Flow<Day> {
        return when (profile.type) {
            ProfileType.STUDENT -> {
                lessonRepository.getLessonsForClass(profile.referenceId, date)
            }
            ProfileType.TEACHER -> {
                lessonRepository.getLessonsForTeacher(profile.referenceId, date)
            }
            ProfileType.ROOM -> {
                lessonRepository.getLessonsForRoom(profile.referenceId, date)
            }
        }.map { dayPair ->
            if (dayPair.first != DayType.DATA) Day(dayType = dayPair.first)
            else {
                Day(
                    dayType = DayType.DATA,
                    lessons = dayPair.second,
                )
            }
        }
    }
}