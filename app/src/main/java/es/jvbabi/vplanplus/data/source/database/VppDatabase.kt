package es.jvbabi.vplanplus.data.source.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.data.model.DbProfile
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.data.source.database.converter.DayDataTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.UuidConverter
import es.jvbabi.vplanplus.data.source.database.crossover.LessonSchoolEntityCrossover
import es.jvbabi.vplanplus.data.source.database.dao.CalendarEventDao
import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.data.source.database.dao.HolidayDao
import es.jvbabi.vplanplus.data.source.database.dao.KeyValueDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonSchoolEntityCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.data.source.database.dao.LogRecordDao
import es.jvbabi.vplanplus.data.source.database.dao.MessageDao
import es.jvbabi.vplanplus.data.source.database.dao.PlanDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDefaultLessonsCrossoverDao
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

        LessonSchoolEntityCrossover::class,
        DbProfileDefaultLesson::class,
        LogRecord::class,
        DbCalendarEvent::class
    ],
    version = 10,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 5, to = 6), // add messages
        AutoMigration(from = 8, to = 9), // primary keys for school entity
        AutoMigration(from = 9, to = 10), // add vppId
    ],
)
@TypeConverters(
    LocalDateConverter::class,
    ProfileTypeConverter::class,
    ProfileCalendarTypeConverter::class,
    UuidConverter::class,
    DayDataTypeConverter::class,
    LocalDateTimeConverter::class
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
    }
}