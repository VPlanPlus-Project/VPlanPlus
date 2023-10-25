package es.jvbabi.vplanplus.domain.repository

interface StudentPlanRepository {
    suspend fun getStudentPlan(schoolId: String, classId: Int, username: String, password: String)
}