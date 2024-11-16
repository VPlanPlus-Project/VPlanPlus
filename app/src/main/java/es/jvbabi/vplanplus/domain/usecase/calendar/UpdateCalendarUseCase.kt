package es.jvbabi.vplanplus.domain.usecase.calendar

import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.model.CalendarEvent
import es.jvbabi.vplanplus.domain.model.ProfileCalendarType
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.Keys
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.util.TimeZone

class UpdateCalendarUseCase(
    private val planRepository: PlanRepository,
    private val profileRepository: ProfileRepository,
    private val calendarRepository: CalendarRepository,
    private val keyValueRepository: KeyValueRepository,
    private val stringRepository: StringRepository
) {
    suspend operator fun invoke() {
        val profiles = profileRepository.getProfiles().firstOrNull().orEmpty()
            .filter { it.calendarType != ProfileCalendarType.NONE && it.calendarId != null }

        val dates = planRepository.getLocalPlanDates()
        val version = keyValueRepository.getOrDefault(Keys.LESSON_VERSION_NUMBER, "0").toLong()
        calendarRepository.deleteAppEvents()
        profiles.forEach { profile ->
            val calendar = calendarRepository.getCalendarById(profile.calendarId!!) ?: return@forEach

            calendarRepository.deleteAppEvents(calendar)
            dates.map {
                planRepository.getDayForProfile(profile, it, version).first()
            }.forEach { day ->
                if (profile.calendarType == ProfileCalendarType.LESSON) {
                    day
                        .getEnabledLessons(profile)
                        .filter { it.displaySubject != "-" }
                        .forEach { lesson ->
                            calendarRepository.insertEvent(
                                CalendarEvent(
                                    startTimeStamp = ZonedDateTimeConverter().zonedDateTimeToTimestamp(
                                        lesson.start
                                    ),
                                    endTimeStamp = ZonedDateTimeConverter().zonedDateTimeToTimestamp(
                                        lesson.end
                                    ),
                                    title = stringRepository.getString(R.string.calendarRecord_title, lesson.displaySubject, lesson.lessonNumber),
                                    calendarId = calendar.id,
                                    location = if (lesson.rooms.isEmpty()) profile.getSchool().name else stringRepository.getString(R.string.calendarRecord_location, lesson.rooms.joinToString(", ") { it.name }, profile.getSchool().name),
                                    timeZone = TimeZone.getTimeZone("UTC"),
                                )
                            )
                        }
                } else {
                    calendarRepository.insertEvent(
                        CalendarEvent(
                            calendarId = calendar.id,
                            startTimeStamp = ZonedDateTimeConverter().zonedDateTimeToTimestamp(
                                day
                                    .getEnabledLessons(profile)
                                    .filter { it.displaySubject != "-" }
                                    .sortedBy { it.lessonNumber }
                                    .first { it.displaySubject != "-" }.start
                            ),
                            endTimeStamp = ZonedDateTimeConverter().zonedDateTimeToTimestamp(
                                day
                                    .getEnabledLessons(profile)
                                    .filter { it.displaySubject != "-" }
                                    .sortedBy { it.lessonNumber }
                                    .last { it.displaySubject != "-" }.end
                            ),
                            info = day.info,
                            location = profile.getSchool().name,
                            title = stringRepository.getString(R.string.calendarRecord_dayTitle, profile.displayName),
                            timeZone = TimeZone.getTimeZone("UTC")
                        )
                    )
                }
            }
        }
    }
}