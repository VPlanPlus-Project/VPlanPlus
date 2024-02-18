package es.jvbabi.vplanplus.shared.data

import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import java.util.UUID

class FakeDefaultLessonRepository(
    private val classRepository: ClassRepository,
    private val teacherRepository: TeacherRepository,
) : DefaultLessonRepository {
    private val defaultLessons = mutableListOf<DbDefaultLesson>()

    override suspend fun insert(defaultLesson: DbDefaultLesson): UUID {
        val id = UUID.randomUUID()
        defaultLessons.add(
            defaultLesson.copy(
                defaultLessonId = id
            )
        )
        return id
    }

    override suspend fun getDefaultLessonByVpId(vpId: Long): DefaultLesson? {
        val dl = defaultLessons
            .firstOrNull { it.vpId == vpId } ?: return null
        return DefaultLesson(
            defaultLessonId = dl.defaultLessonId,
            vpId = dl.vpId,
            `class` = classRepository.getClassById(dl.classId)!!,
            teacher = if (dl.teacherId != null) teacherRepository.getTeacherById(dl.teacherId!!) else null,
            subject = dl.subject
        )
    }


    override suspend fun getDefaultLessonByClassId(classId: UUID): List<DefaultLesson> {
        return defaultLessons
            .filter { it.classId == classId }
            .map {
                DefaultLesson(
                    defaultLessonId = it.defaultLessonId,
                    vpId = it.vpId,
                    `class` = classRepository.getClassById(it.classId)!!,
                    teacher = if (it.teacherId != null) teacherRepository.getTeacherById(it.teacherId!!) else null,
                    subject = it.subject
                )
            }
    }

    override suspend fun updateTeacherId(classId: UUID, vpId: Long, teacherId: UUID) {
        val dl = defaultLessons
            .firstOrNull { it.vpId == vpId && it.classId == classId } ?: return
        defaultLessons.remove(dl)
        defaultLessons.add(
            dl.copy(
                teacherId = teacherId
            )
        )
    }

}