package es.jvbabi.vplanplus.data.source

import androidx.room.Database
import androidx.room.RoomDatabase
import es.jvbabi.vplanplus.domain.model.Classes
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.KeyValue
import es.jvbabi.vplanplus.domain.model.Lesson
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.Room
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Teacher
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.Week

@Database(
    entities = [
        Classes::class,
        Lesson::class,
        Profile::class,
        Room::class,
        School::class,
        Teacher::class,
        KeyValue::class,
        Holiday::class,
        Week::class,
        LessonTime::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class VppDatabase : RoomDatabase() {
    abstract val schoolDao: SchoolDao
    abstract val profileDao: ProfileDao
    abstract val classDao: ClassDao
    abstract val keyValueDao: KeyValueDao
    abstract val holidayDao: HolidayDao
    abstract val weekDao: WeekDao
    abstract val teacherDao: TeacherDao
    abstract val lessonDao: LessonDao
    abstract val roomDao: RoomDao
    abstract val lessonTimeDao: LessonTimeDao
}