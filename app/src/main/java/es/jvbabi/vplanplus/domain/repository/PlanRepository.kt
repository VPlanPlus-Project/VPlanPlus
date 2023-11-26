package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface PlanRepository {

    fun getDayForProfile(profile: Profile, date: LocalDate, version: Long): Flow<Day>
    fun getDayForTeacher(teacherId: Long, date: LocalDate, version: Long): Flow<Day>
    fun getDayForClass(classId: Long, date: LocalDate, version: Long): Flow<Day>
    fun getDayForRoom(roomId: Long, date: LocalDate, version: Long): Flow<Day>
}