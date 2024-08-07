package es.jvbabi.vplanplus.data.source.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import es.jvbabi.vplanplus.data.model.DbDefaultLesson
import es.jvbabi.vplanplus.data.model.DbGroup
import es.jvbabi.vplanplus.data.model.DbLesson
import es.jvbabi.vplanplus.data.model.DbPlanData
import es.jvbabi.vplanplus.data.model.DbProfileDefaultLesson
import es.jvbabi.vplanplus.data.model.DbRoom
import es.jvbabi.vplanplus.data.model.DbRoomBooking
import es.jvbabi.vplanplus.data.model.DbSchoolEntity
import es.jvbabi.vplanplus.data.model.DbVppId
import es.jvbabi.vplanplus.data.model.DbVppIdToken
import es.jvbabi.vplanplus.data.model.homework.DbHomework
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkDocument
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkProfileData
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTask
import es.jvbabi.vplanplus.data.model.homework.DbHomeworkTaskDone
import es.jvbabi.vplanplus.data.model.profile.DbClassProfile
import es.jvbabi.vplanplus.data.model.profile.DbRoomProfile
import es.jvbabi.vplanplus.data.model.profile.DbTeacherProfile
import es.jvbabi.vplanplus.data.source.database.converter.DayDataTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.GradeModifierConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.SchoolDownloadTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.UuidConverter
import es.jvbabi.vplanplus.data.source.database.converter.VppIdStateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.data.source.database.crossover.LessonRoomCrossover
import es.jvbabi.vplanplus.data.source.database.crossover.LessonTeacherCrossover
import es.jvbabi.vplanplus.data.source.database.dao.DefaultLessonDao
import es.jvbabi.vplanplus.data.source.database.dao.GroupDao
import es.jvbabi.vplanplus.data.source.database.dao.HolidayDao
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDao
import es.jvbabi.vplanplus.data.source.database.dao.HomeworkDocumentDao
import es.jvbabi.vplanplus.data.source.database.dao.KeyValueDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonSchoolEntityCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.LessonTimeDao
import es.jvbabi.vplanplus.data.source.database.dao.LogRecordDao
import es.jvbabi.vplanplus.data.source.database.dao.MessageDao
import es.jvbabi.vplanplus.data.source.database.dao.PlanDao
import es.jvbabi.vplanplus.data.source.database.dao.PreferredHomeworkNotificationTimeDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDao
import es.jvbabi.vplanplus.data.source.database.dao.ProfileDefaultLessonsCrossoverDao
import es.jvbabi.vplanplus.data.source.database.dao.RoomBookingDao
import es.jvbabi.vplanplus.data.source.database.dao.RoomDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolDao
import es.jvbabi.vplanplus.data.source.database.dao.SchoolEntityDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdDao
import es.jvbabi.vplanplus.data.source.database.dao.VppIdTokenDao
import es.jvbabi.vplanplus.domain.model.DbSchool
import es.jvbabi.vplanplus.domain.model.Holiday
import es.jvbabi.vplanplus.domain.model.KeyValue
import es.jvbabi.vplanplus.domain.model.LessonTime
import es.jvbabi.vplanplus.domain.model.LogRecord
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbGrade
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbInterval
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbSubject
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbTeacher
import es.jvbabi.vplanplus.feature.main_grades.view.data.model.DbYear
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.GradeDao
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.SubjectDao
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.TeacherDao
import es.jvbabi.vplanplus.feature.main_grades.view.data.source.database.YearDao
import es.jvbabi.vplanplus.feature.main_homework.shared.data.model.DbPreferredNotificationTime

@Database(
    entities = [
        DbLesson::class,
        DbClassProfile::class,
        DbTeacherProfile::class,
        DbRoomProfile::class,
        DbSchool::class,
        KeyValue::class,
        Holiday::class,
        LessonTime::class,
        DbDefaultLesson::class,
        DbPlanData::class,
        DbSchoolEntity::class,
        DbRoom::class,
        DbGroup::class,
        Message::class,
        DbVppId::class,
        DbVppIdToken::class,
        DbRoomBooking::class,
        LessonTeacherCrossover::class,
        LessonRoomCrossover::class,
        DbProfileDefaultLesson::class,
        LogRecord::class,

        DbHomework::class,
        DbHomeworkProfileData::class,
        DbHomeworkTask::class,
        DbHomeworkTaskDone::class,
        DbHomeworkDocument::class,
        DbPreferredNotificationTime::class,

        DbSubject::class,
        DbTeacher::class,
        DbGrade::class,
        DbYear::class,
        DbInterval::class
    ],
    version = 38,
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
        AutoMigration(from = 21, to = 22), // add preferred notification time
        AutoMigration(from = 25, to = 26), // add cached-at attribute to vpp.ID
        AutoMigration(from = 26, to = 27), // add vpp.ID to profile
        AutoMigration(from = 30, to = 31), // add documents
        AutoMigration(from = 36, to = 37), // add courseGroup
    ],
)
@TypeConverters(
    LocalDateConverter::class,
    ProfileCalendarTypeConverter::class,
    UuidConverter::class,
    DayDataTypeConverter::class,
    VppIdStateConverter::class,
    GradeModifierConverter::class,
    ZonedDateTimeConverter::class,
    SchoolDownloadTypeConverter::class
)
abstract class VppDatabase : RoomDatabase() {
    abstract val schoolDao: SchoolDao
    abstract val profileDao: ProfileDao
    abstract val keyValueDao: KeyValueDao
    abstract val holidayDao: HolidayDao
    abstract val lessonDao: LessonDao
    abstract val lessonTimeDao: LessonTimeDao
    abstract val schoolEntityDao: SchoolEntityDao
    abstract val groupDao: GroupDao
    abstract val lessonSchoolEntityCrossoverDao: LessonSchoolEntityCrossoverDao
    abstract val logRecordDao: LogRecordDao
    abstract val defaultLessonDao: DefaultLessonDao
    abstract val profileDefaultLessonsCrossoverDao: ProfileDefaultLessonsCrossoverDao
    abstract val planDao: PlanDao
    abstract val messageDao: MessageDao
    abstract val vppIdDao: VppIdDao
    abstract val vppIdTokenDao: VppIdTokenDao
    abstract val roomBookingDao: RoomBookingDao
    abstract val roomDao: RoomDao

    abstract val homeworkDao: HomeworkDao
    abstract val homeworkDocumentDao: HomeworkDocumentDao
    abstract val homeworkNotificationTimeDao: PreferredHomeworkNotificationTimeDao

    // grades
    abstract val subjectDao: SubjectDao
    abstract val teacherDao: TeacherDao
    abstract val gradeDao: GradeDao
    abstract val yearDao: YearDao

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
                db.execSQL("ALTER TABLE messages RENAME COLUMN schoolId TO sid_old")
                db.execSQL("ALTER TABLE messages ADD COLUMN schoolId INT NULL")
                db.execSQL("UPDATE messages SET schoolId = sid_old")
                db.execSQL("ALTER TABLE messages DROP COLUMN sid_old")
            }
        }

        val migration_22_23 = object : Migration(22, 23) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS calendar_events")
                db.execSQL("ALTER TABLE keyValue RENAME TO key_value")
            }
        }

        val migration_23_24 = object : Migration(23, 24) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE homework RENAME COLUMN defaultLessonVpId TO defaultLessonVpId_old")
                db.execSQL("ALTER TABLE homework ADD COLUMN defaultLessonVpId INTEGER NULL")
                db.execSQL("UPDATE homework SET defaultLessonVpId = defaultLessonVpId_old")
                db.execSQL("ALTER TABLE homework DROP COLUMN defaultLessonVpId_old")
            }
        }

        val migration_24_25 = object : Migration(24, 25) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE `grade`")
                db.execSQL("CREATE TABLE IF NOT EXISTS `bs_years` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `from` INTEGER NOT NULL, `to` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                db.execSQL("CREATE TABLE IF NOT EXISTS `bs_intervals` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `from` INTEGER NOT NULL, `to` INTEGER NOT NULL, `includedIntervalId` INTEGER, `yearId` INTEGER NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`yearId`) REFERENCES `bs_years`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
                db.execSQL("CREATE TABLE IF NOT EXISTS `grade` (`id` INTEGER NOT NULL, `givenAt` INTEGER NOT NULL, `givenBy` INTEGER NOT NULL, `subject` INTEGER NOT NULL, `value` REAL NOT NULL, `type` TEXT NOT NULL, `comment` TEXT NOT NULL, `modifier` INTEGER NOT NULL, `vppId` INTEGER NOT NULL, `interval` INTEGER NOT NULL, PRIMARY KEY(`id`, `givenBy`, `subject`, `vppId`), FOREIGN KEY(`givenBy`) REFERENCES `grade_teacher`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION , FOREIGN KEY(`subject`) REFERENCES `grade_subject`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`vppId`) REFERENCES `vpp_id`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`interval`) REFERENCES `bs_intervals`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_grade_id` ON `grade` (`id`)")
            }
        }

        val migration_27_28 = object : Migration(27, 28) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE school ADD COLUMN credentials_valid INTEGER DEFAULT NULL")
            }
        }

        val migration_28_29 = object : Migration(28, 29) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE homework RENAME TO homework_old")
                db.execSQL("ALTER TABLE homework_old ADD COLUMN profile_id TEXT DEFAULT NULL")
                db.execSQL("CREATE TABLE `homework` (`id` INTEGER NOT NULL, `createdBy` INTEGER, `classes` TEXT NOT NULL, `isPublic` INTEGER NOT NULL DEFAULT false, `createdAt` INTEGER NOT NULL, `defaultLessonVpId` INTEGER, `until` INTEGER NOT NULL, `hidden` INTEGER NOT NULL DEFAULT false, `profile_id` TEXT NOT NULL, PRIMARY KEY(`id`), FOREIGN KEY(`createdBy`) REFERENCES `vpp_id`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`profile_id`) REFERENCES `profile`(`profileId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`classes`) REFERENCES `school_entity`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
                db.execSQL("UPDATE homework_old SET profile_id = (SELECT profileId FROM profile WHERE linkedVppId = homework_old.createdBy LIMIT 1) WHERE profile_id IS NULL")
                db.execSQL("UPDATE homework_old SET profile_id = (SELECT profileId FROM profile WHERE referenceId = homework_old.classes LIMIT 1)")
                db.execSQL("INSERT INTO homework (id, createdBy, classes, isPublic, createdAt, defaultLessonVpId, until, hidden, profile_id) SELECT * FROM homework_old WHERE homework_old.profile_id IS NOT NULL")
                db.execSQL("DROP TABLE homework_old")
            }
        }

        val migration_29_30 = object : Migration(29, 30) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE profile ADD COLUMN is_homework_enabled INTEGER NOT NULL DEFAULT true")
            }
        }

        val migration_37_38 = object : Migration(37, 38) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE school RENAME TO school_old;")
                db.execSQL("ALTER TABLE school_old ADD COLUMN school_download_mode TEXT DEFAULT NULL;")
                db.execSQL("UPDATE school_old SET school_download_mode = 'INDIWARE_WOCHENPLAN_6';")
                db.execSQL("CREATE TABLE `school` (`id` INTEGER NOT NULL, `sp24_school_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `username` TEXT NOT NULL, `password` TEXT NOT NULL, `days_per_week` INTEGER NOT NULL, `fully_compatible` INTEGER NOT NULL, `credentials_valid` INTEGER DEFAULT NULL, `school_download_mode` TEXT NOT NULL, PRIMARY KEY(`id`));")
                db.execSQL("DROP INDEX IF EXISTS `index_school_id`")
                db.execSQL("CREATE UNIQUE INDEX `index_school_id` ON `school` (`id`)")
                db.execSQL("INSERT INTO school (id, sp24_school_id, name, username, password, days_per_week, fully_compatible, credentials_valid, school_download_mode) SELECT * FROM school_old;")
                db.execSQL("DROP TABLE school_old;")
            }
        }
    }
}