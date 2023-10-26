package es.jvbabi.vplanplus.domain.usecase

import es.jvbabi.vplanplus.domain.OnlineResponse
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.xml.VPlanData
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import java.time.LocalDate

class VPlanUseCases(
    private val vPlanRepository: VPlanRepository,
    private val defaultLessonRepository: DefaultLessonRepository,
    private val classRepository: ClassRepository,
    private val teacherReository: TeacherRepository
) {
    suspend fun getVPlanData(school: School, date: LocalDate): OnlineResponse<VPlanData?> {
        return vPlanRepository.getVPlanData(school, date)
    }

    suspend fun processVplanData(vPlanData: VPlanData) {
        vPlanData.wPlanDataObject.classes!!.forEach {

            // get class or create if not exists
            val dbClass = try {
                classRepository.getClassById(classRepository.getClassIdBySchoolIdAndClassName(vPlanData.schoolId, it.schoolClass))
            } catch (e: NullPointerException) {
                classRepository.createClass(
                    schoolId = vPlanData.schoolId,
                    className = it.schoolClass
                )
                classRepository.getClassById(classRepository.getClassIdBySchoolIdAndClassName(vPlanData.schoolId, it.schoolClass))
            }

            // set default lessons
            it.defaultLessons!!.forEach { defaultLessonWrapper ->
                // get or create teacher
                var teacher = teacherReository.find(schoolId = vPlanData.schoolId, acronym = defaultLessonWrapper.defaultLesson!!.teacherShort!!)
                if (teacher == null) {
                    teacherReository.createTeacher(schoolId = vPlanData.schoolId, acronym = defaultLessonWrapper.defaultLesson!!.teacherShort!!)
                    teacher = teacherReository.find(schoolId = vPlanData.schoolId, acronym = defaultLessonWrapper.defaultLesson!!.teacherShort!!)!!
                }

                // create default lesson
                val defaultLesson = DefaultLesson(
                    schoolId = vPlanData.schoolId,
                    vpId = defaultLessonWrapper.defaultLesson!!.lessonId!!,
                    subject = defaultLessonWrapper.defaultLesson!!.subjectShort!!,
                    teacherId = teacher.id!!,
                )
                defaultLessonRepository.updateDefaultLesson(defaultLesson)
            }

            // set actual lessons
        }
    }
}