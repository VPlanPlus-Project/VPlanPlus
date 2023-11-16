package es.jvbabi.vplanplus.data.source.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import es.jvbabi.vplanplus.data.model.DbClass
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbTeacher
import es.jvbabi.vplanplus.data.source.database.converter.DayConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileTypeConverter
import es.jvbabi.vplanplus.data.source.database.crossover.LessonRoomCrossover
import es.jvbabi.vplanplus.data.source.database.crossover.LessonTeacherCrossover
import es.jvbabi.vplanplus.data.source.database.dao.CalendarEventDao
import es.jvbabi.vplanplus.data.source.database.dao.ClassDao
import es.jvbabi.vplanplus.data.source.database.dao.DbLessonDao
import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.data.source.database.dao.HolidayDao
import es.jvbabi.vplanplus.data.source.database.dao.KeyValueDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonRoomCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTeacherCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.data.source.database.dao.LogRecordDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.RoomDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolDao
import es.jvbabi.vplanplus.data.source.database.dao.TeacherDao
import es.jvbabi.vplanplus.data.source.database.dao.WeekDao
import es.jvbabi.vplanplus.domain.model.DbCalendarEvent
import es.jvbabi.vplanplus.domain.model.DefaultLesson
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.KeyValue
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.LogRecord
import es.jvbabi.vplanplus.domain.model.Profile
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Week

@Database(
    entities = [
        DbClass::class,
        DbLesson::class,
        Profile::class,
        DbRoom::class,
        School::class,
        DbTeacher::class,
        KeyValue::class,
        Holiday::class,
        Week::class,
        LessonTime::class,
        DefaultLesson::class,

        LessonRoomCrossover::class,
        LessonTeacherCrossover::class,
        LogRecord::class,
        DbCalendarEvent::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(
    DayConverter::class,
    ProfileTypeConverter::class,
    ProfileCalendarTypeConverter::class
)
abstract class VppDatabase : RoomDatabase() {
    abstract val schoolDao: SchoolDao
    abstract val profileDao: ProfileDao
    abstract val classDao: ClassDao
    abstract val keyValueDao: KeyValueDao
    abstract val holidayDao: HolidayDao
    abstract val weekDao: WeekDao
    abstract val teacherDao: TeacherDao
    abstract val lessonDao: DbLessonDao
    abstract val roomDao: RoomDao
    abstract val lessonTimeDao: LessonTimeDao
    abstract val lessonRoomCrossoverDao: LessonRoomCrossoverDao
    abstract val lessonTeacherCrossoverDao: LessonTeacherCrossoverDao
    abstract val logRecordDao: LogRecordDao
    abstract val calendarEventDao: CalendarEventDao
    abstract val defaultLessonDao: DefaultLessonDao
}