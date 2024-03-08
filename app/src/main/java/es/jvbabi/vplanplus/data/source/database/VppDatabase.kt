package es.jvbabi.vplanplus.data.source.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbHomework
import es.jvbabi.vplanplus.data.model.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.data.source.database.converter.DayDataTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.GradeModifierConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.UuidConverter
import es.jvbabi.vplanplus.data.source.database.converter.VppIdStateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.crossover.LessonSchoolEntityCrossover
import es.jvbabi.vplanplus.data.source.database.dao.CalendarEventDao
import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.data.source.database.dao.HolidayDao
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.data.source.database.dao.KeyValueDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonSchoolEntityCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.data.source.database.dao.LogRecordDao
import es.jvbabi.vplanplus.data.source.database.dao.MessageDao
import es.jvbabi.vplanplus.data.source.database.dao.PlanDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDefaultLessonsCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.data.source.database.dao.WeekDao
import es.jvbabi.vplanplus.domain.model.DbCalendarEvent
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.KeyValue
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.LogRecord
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.domain.model.School
import es.jvbabi.vplanplus.domain.model.Week
import es.jvbabi.vplanplus.feature.grades.data.model.DbGrade
import es.jvbabi.vplanplus.feature.grades.data.model.DbSubject
import es.jvbabi.vplanplus.feature.grades.data.model.DbTeacher
import es.jvbabi.vplanplus.feature.grades.data.source.database.GradeDao
import es.jvbabi.vplanplus.feature.grades.data.source.database.SubjectDao
import es.jvbabi.vplanplus.feature.grades.data.source.database.TeacherDao

@Database(
    entities = [
        DbLesson::class,
        DbProfile::class,
        School::class,
        KeyValue::class,
        Holiday::class,
        Week::class,
        LessonTime::class,
        DbDefaultLesson::class,
        DbPlanData::class,
        DbSchoolEntity::class,
        Message::class,
        DbVppId::class,
        DbVppIdToken::class,
        DbRoomBooking::class,
        LessonSchoolEntityCrossover::class,
        DbProfileDefaultLesson::class,
        LogRecord::class,
        DbCalendarEvent::class,
        DbHomework::class,
        DbHomeworkTask::class,

        DbSubject::class,
        DbTeacher::class,
        DbGrade::class
    ],
    version = 21,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 5, to = 6), // add messages
        AutoMigration(from = 8, to = 9), // primary keys for school entity
        AutoMigration(from = 9, to = 10), // add vppId
        AutoMigration(from = 13, to = 14), // indices changed for DbGrade
        AutoMigration(from = 14, to = 15), // vpp.ID Email
        AutoMigration(from = 15, to = 16), // add homework
        AutoMigration(from = 16, to = 17), // add homework_task.individualId
        AutoMigration(from = 17, to = 18), // add homework.isPublic
        AutoMigration(from = 18, to = 19), // add zoned date time
        AutoMigration(from = 19, to = 20), // add homework can hide
    ],
)
@TypeConverters(
    LocalDateConverter::class,
    ProfileTypeConverter::class,
    ProfileCalendarTypeConverter::class,
    UuidConverter::class,
    DayDataTypeConverter::class,
    LocalDateTimeConverter::class,
    VppIdStateConverter::class,
    GradeModifierConverter::class,
    ZonedDateTimeConverter::class
)
abstract class VppDatabase : RoomDatabase() {
    abstract val schoolDao: SchoolDao
    abstract val profileDao: ProfileDao
    abstract val keyValueDao: KeyValueDao
    abstract val holidayDao: HolidayDao
    abstract val weekDao: WeekDao
    abstract val lessonDao: LessonDao
    abstract val lessonTimeDao: LessonTimeDao
    abstract val schoolEntityDao: SchoolEntityDao
    abstract val lessonSchoolEntityCrossoverDao: LessonSchoolEntityCrossoverDao
    abstract val logRecordDao: LogRecordDao
    abstract val calendarEventDao: CalendarEventDao
    abstract val defaultLessonDao: DefaultLessonDao
    abstract val profileDefaultLessonsCrossoverDao: ProfileDefaultLessonsCrossoverDao
    abstract val planDao: PlanDao
    abstract val messageDao: MessageDao
    abstract val vppIdDao: VppIdDao
    abstract val vppIdTokenDao: VppIdTokenDao
    abstract val roomBookingDao: RoomBookingDao
    abstract val homeworkDao: HomeworkDao

    // grades
    abstract val subjectDao: SubjectDao
    abstract val teacherDao: TeacherDao
    abstract val gradeDao: GradeDao

    companion object {
        val migration_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE messages ADD COLUMN notificationSent INTEGER NOT NULL DEFAULT 0")
            }
        }
        val migration_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE lesson_time ADD COLUMN start_unix_timestamp INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE lesson_time ADD COLUMN end_unix_timestamp INTEGER NOT NULL DEFAULT 0")

                db.execSQL("UPDATE lesson_time SET start_unix_timestamp = (strftime('%s', '1970-01-01 ' || start))")
                db.execSQL("UPDATE lesson_time SET end_unix_timestamp = (strftime('%s', '1970-01-01 ' || end))")

                db.execSQL("ALTER TABLE lesson_time DROP COLUMN start")
                db.execSQL("ALTER TABLE lesson_time DROP COLUMN end")

                db.execSQL("ALTER TABLE lesson_time RENAME COLUMN start_unix_timestamp TO start")
                db.execSQL("ALTER TABLE lesson_time RENAME COLUMN end_unix_timestamp TO end")
            }
        }

        val migration_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                try {
                    db.execSQL("ALTER TABLE vpp_id_token ADD COLUMN bsToken TEXT DEFAULT NULL")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                db.execSQL("CREATE TABLE IF NOT EXISTS grade_teacher (" +
                        "id INTEGER PRIMARY KEY NOT NULL," +
                        "firstname TEXT NOT NULL," +
                        "lastname TEXT NOT NULL)")

                db.execSQL("CREATE TABLE IF NOT EXISTS grade_subject (" +
                        "id INTEGER PRIMARY KEY NOT NULL," +
                        "short TEXT NOT NULL," +
                        "name TEXT NOT NULL)")

                db.execSQL("CREATE TABLE IF NOT EXISTS grade (" +
                        "id INTEGER PRIMARY KEY NOT NULL ," +
                        "givenAt INTEGER NOT NULL," +
                        "givenBy INTEGER NOT NULL," +
                        "subject INTEGER NOT NULL," +
                        "value REAL NOT NULL," +
                        "modifier INTEGER NOT NULL," +
                        "vppId INTEGER NOT NULL," +
                        "FOREIGN KEY (givenBy) REFERENCES grade_teacher(id)," +
                        "FOREIGN KEY (subject) REFERENCES grade_subject(id)," +
                        "FOREIGN KEY (vppId) REFERENCES vpp_id(id))")

                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_grade_teacher_id ON grade_teacher(id)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_grade_subject_id ON grade_subject(id)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_grade_id ON grade(id)")
            }
        }

        val migration_11_12 = object : Migration(11, 12) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE grade_teacher ADD COLUMN short TEXT NOT NULL DEFAULT '???'")
            }
        }

        val migration_12_13 = object : Migration(12, 13) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE grade ADD COLUMN type TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE grade ADD COLUMN comment TEXT NOT NULL DEFAULT ''")
            }
        }

        val migration_20_21 = object : Migration(20, 21) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE homework_task DROP COLUMN individualId")
            }
        }
    }
}