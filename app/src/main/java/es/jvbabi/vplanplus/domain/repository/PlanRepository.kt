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
    fun getDayForGroup(groupId: Int, date: LocalDate, version: Long): Flow<Day>
    fun getDayForRoom(roomId: Int, date: LocalDate, version: Long): Flow<Day>

    fun getDayInfoForSchool(schoolId: Int, date: LocalDate, version: Long): Flow<Plan?>

    suspend fun createPlan(plan: Plan)
    suspend fun deleteAllPlans()
    suspend fun deletePlansByVersion(version: Long)
    suspend fun getLocalPlanDates(): List<LocalDate>
    suspend fun getLocalPlaDatesForSchool(schoolId: Int): List<LocalDate>
}