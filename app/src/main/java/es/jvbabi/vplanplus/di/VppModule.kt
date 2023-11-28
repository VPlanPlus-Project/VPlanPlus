package es.jvbabi.vplanplus.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.data.repository.BaseDataRepositoryImpl
import es.jvbabi.vplanplus.data.repository.CalendarRepositoryImpl
import es.jvbabi.vplanplus.data.repository.ClassRepositoryImpl
import es.jvbabi.vplanplus.data.repository.DefaultLessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.HolidayRepositoryImpl
import es.jvbabi.vplanplus.data.repository.KeyValueRepositoryImpl
import es.jvbabi.vplanplus.data.repository.LessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.LessonTimeRepositoryImpl
import es.jvbabi.vplanplus.data.repository.LogRepositoryImpl
import es.jvbabi.vplanplus.data.repository.PlanRepositoryImpl
import es.jvbabi.vplanplus.data.repository.ProfileRepositoryImpl
import es.jvbabi.vplanplus.data.repository.RoomRepositoryImpl
import es.jvbabi.vplanplus.data.repository.SchoolRepositoryImpl
import es.jvbabi.vplanplus.data.repository.TeacherRepositoryImpl
import es.jvbabi.vplanplus.data.repository.VPlanRepositoryImpl
import es.jvbabi.vplanplus.data.repository.WeekRepositoryImpl
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.data.source.database.converter.DayConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.UuidConverter
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.domain.usecase.BaseDataUseCases
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.HolidayUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.RoomUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VppModule {

    // Database
    @Provides
    @Singleton
    fun provideVppDatabase(app: Application): VppDatabase {
        return Room.databaseBuilder(
            app,
            VppDatabase::class.java,
            "vpp.db"
        )
            .fallbackToDestructiveMigration() // TODO: Remove for production
            .addTypeConverter(DayConverter())
            .addTypeConverter(ProfileTypeConverter())
            .addTypeConverter(UuidConverter())
            .addTypeConverter(ProfileCalendarTypeConverter())
            .allowMainThreadQueries()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }

    // Repositories
    @Provides
    @Singleton
    fun provideSchoolRepository(db: VppDatabase): SchoolRepository {
        return SchoolRepositoryImpl(db.schoolDao)
    }

    @Provides
    @Singleton
    fun provideCalendarRepository(@ApplicationContext context: Context, db: VppDatabase): CalendarRepository {
        return CalendarRepositoryImpl(
            context = context,
            calendarEventDao = db.calendarEventDao
        )
    }

    @Provides
    @Singleton
    fun provideLogRepository(db: VppDatabase): LogRecordRepository {
        return LogRepositoryImpl(db.logRecordDao)
    }

    @Provides
    @Singleton
    fun provideKeyValueRepository(db: VppDatabase): KeyValueRepository {
        return KeyValueRepositoryImpl(db.keyValueDao)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(db: VppDatabase): ProfileRepository {
        return ProfileRepositoryImpl(db.profileDao, db.profileDefaultLessonsCrossoverDao)
    }

    @Provides
    @Singleton
    fun provideClassRepository(db: VppDatabase): ClassRepository {
        return ClassRepositoryImpl(db.classDao)
    }

    @Provides
    @Singleton
    fun provideHolidayRepository(db: VppDatabase): HolidayRepository {
        return HolidayRepositoryImpl(db.holidayDao, provideSchoolRepository(db))
    }

    @Provides
    @Singleton
    fun provideWeekRepository(db: VppDatabase): WeekRepository {
        return WeekRepositoryImpl(db.weekDao)
    }

    @Provides
    @Singleton
    fun provideLessonTimeRepository(db: VppDatabase): LessonTimeRepository {
        return LessonTimeRepositoryImpl(db.lessonTimeDao)
    }

    @Provides
    @Singleton
    fun provideBaseDataRepository(
        classRepository: ClassRepository,
        lessonTimeRepository: LessonTimeRepository,
        holidayRepository: HolidayRepository,
        weekRepository: WeekRepository,
        roomRepository: RoomRepository,
        teacherRepository: TeacherRepository,
        logRecordRepository: LogRecordRepository
    ): BaseDataRepository {
        return BaseDataRepositoryImpl(classRepository, lessonTimeRepository, holidayRepository, weekRepository, roomRepository, teacherRepository, logRecordRepository)
    }

    @Provides
    @Singleton
    fun provideVPlanRepository(
        logRecordRepository: LogRecordRepository
    ): VPlanRepository {
        return VPlanRepositoryImpl(logRecordRepository)
    }

    @Provides
    @Singleton
    fun provideTeacherRepository(db: VppDatabase): TeacherRepository {
        return TeacherRepositoryImpl(db.teacherDao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideLessonRepository(db: VppDatabase): LessonRepository {
        return LessonRepositoryImpl(
            lessonDao = db.lessonDao,
        )
    }

    @Provides
    @Singleton
    fun providePlanRepository(db: VppDatabase): PlanRepository {
        return PlanRepositoryImpl(
            holidayRepository = provideHolidayRepository(db),
            teacherRepository = provideTeacherRepository(db),
            classRepository = provideClassRepository(db),
            roomRepository = provideRoomRepository(db),
            lessonRepository = provideLessonRepository(db)
        )
    }

    @Provides
    @Singleton
    fun provideRoomRepository(db: VppDatabase): RoomRepository {
        return RoomRepositoryImpl(db.roomDao)
    }

    @Provides
    @Singleton
    fun provideDefaultLessonRepository(db: VppDatabase): DefaultLessonRepository {
        return DefaultLessonRepositoryImpl(db.defaultLessonDao)
    }

    // Use cases

    @Provides
    @Singleton
    fun provideSchoolUseCases(repository: SchoolRepository): SchoolUseCases {
        return SchoolUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideKeyValueUseCases(repository: KeyValueRepository): KeyValueUseCases {
        return KeyValueUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideLessonUseCases(
        lessonRepository: LessonRepository,
        planRepository: PlanRepository,
    ): LessonUseCases {
        return LessonUseCases(
            lessonRepository = lessonRepository,
            planRepository = planRepository
        )
    }

    @Provides
    @Singleton
    fun provideRoomUseCases(
        roomRepository: RoomRepository,
        lessonUseCases: LessonUseCases,
        keyValueUseCases: KeyValueUseCases
    ): RoomUseCases {
        return RoomUseCases(
            roomRepository = roomRepository,
            lessonUseCases = lessonUseCases,
            keyValueUseCases = keyValueUseCases
        )
    }

    @Provides
    @Singleton
    fun provideProfileUseCases(
        repository: ProfileRepository,
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        planRepository: PlanRepository,
        calendarRepository: CalendarRepository
    ): ProfileUseCases {
        return ProfileUseCases(
            profileRepository = repository,
            keyValueRepository = keyValueRepository,
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository,
            planRepository = planRepository,
            calendarRepository = calendarRepository
        )
    }

    @Provides
    @Singleton
    fun provideClassUseCases(repository: ClassRepository): ClassUseCases {
        return ClassUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideHolidayUseCases(repository: HolidayRepository): HolidayUseCases {
        return HolidayUseCases(repository)
    }

    @Provides
    @Singleton
    fun provideBaseDataUseCases(
        baseDataRepository: BaseDataRepository,
    ): BaseDataUseCases {
        return BaseDataUseCases(baseDataRepository)
    }

    @Provides
    @Singleton
    fun provideVPlanUseCases(
        vPlanRepository: VPlanRepository,
        lessonRepository: LessonRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        schoolRepository: SchoolRepository,
        defaultLessonRepository: DefaultLessonRepository,
        db: VppDatabase
    ): VPlanUseCases {
        return VPlanUseCases(
            vPlanRepository = vPlanRepository,
            lessonRepository = lessonRepository,
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository,
            schoolRepository = schoolRepository,
            defaultLessonRepository = defaultLessonRepository,
            lessonRoomCrossover = db.lessonRoomCrossoverDao,
            lessonTeacherCrossover = db.lessonTeacherCrossoverDao,
            keyValueUseCases = provideKeyValueUseCases(provideKeyValueRepository(db))
        )
    }
}