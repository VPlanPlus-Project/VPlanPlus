package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import java.util.UUID

class FakeTeacherRepository(
    private val fakeSchoolRepository: FakeSchoolRepository
) : TeacherRepository {
    private val teachers = mutableListOf<Teacher>()

    override suspend fun createTeacher(schoolId: Long, acronym: String) {
        val school = fakeSchoolRepository.getSchoolFromId(schoolId)!!
        teachers.add(Teacher(UUID.randomUUID(), acronym, school))
    }

    override suspend fun getTeachersBySchoolId(schoolId: Long): List<Teacher> {
        return teachers.filter { it.school.schoolId == schoolId }
    }

    override suspend fun find(
        school: School,
        acronym: String,
        createIfNotExists: Boolean
    ): Teacher? {
        val teacher = teachers.firstOrNull { it.school == school && it.acronym == acronym }
        if (teacher == null && createIfNotExists) {
            createTeacher(school.schoolId, acronym)
            return find(school, acronym, false)
        }
        return teacher
    }

    override suspend fun getTeacherById(id: UUID): Teacher? {
        return teachers.firstOrNull { it.teacherId == id }
    }

    override suspend fun deleteTeachersBySchoolId(schoolId: Long) {
        teachers.removeIf { it.school.schoolId == schoolId }
    }

    override suspend fun insertTeachersByAcronym(schoolId: Long, teachers: List<String>) {
        teachers.forEach { createTeacher(schoolId, it) }
    }

    companion object {
        val teacherNames = listOf(
            "Ack",
            "Ad",
            "Alb",
            "Bau",
            "Ber",
            "Bl",
            "Bü",
            "Czi",
            "De",
            "Do",
            "Dre",
            "Fr",
            "Frö",
            "Gatt",
            "Ger",
            "Gru",
            "Gun",
            "Hau",
            "He",
            "Her",
            "Hö",
            "Hof",
            "Ilg",
            "Irr",
            "Ju",
            "Jü",
            "Kin",
            "Kl",
            "Krü",
            "Leh",
            "Lem",
            "Lu",
            "Mar",
            "Mey",
            "No",
            "Ol",
            "Ot",
            "Pau",
            "Ra",
            "Re",
            "Red",
            "Rei",
            "Ri",
            "Ru",
            "Sa",
            "Sei",
            "Sipp",
            "Snei",
            "Spe",
            "Srei",
            "Stei",
            "Tau",
            "Tei",
            "Tipp",
            "Uhl",
            "Ung",
            "Vo",
            "Wi",
            "Wün",
            "Ze",
            "Zo",
            "Zu",
        )
    }
}