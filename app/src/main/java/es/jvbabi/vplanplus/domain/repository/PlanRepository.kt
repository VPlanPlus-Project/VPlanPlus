package es.jvbabi.vplanplus.domain.repository

import es.jvbabi.vplanplus.domain.model.Day
import es.jvbabi.vplanplus.domain.model.Plan
import es.jvbabi.vplanplus.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

interface PlanRepository {

    fun getDayForProfile(profile: Profile, date: LocalDate, version: Long): Flow<Day>
    fun getDayForTeacher(teacherId: UUID, date: LocalDate, version: Long): Flow<Day>
    fun getDayForClass(classId: UUID, date: LocalDate, version: Long): Flow<Day>
    fun getDayForRoom(roomId: UUID, date: LocalDate, version: Long): Flow<Day>

    suspend fun createPlan(plan: Plan)
    suspend fun deleteAllPlans()
    suspend fun deletePlansByVersion(version: Long)
    suspend fun getLocalPlanDates(): List<LocalDate>
}