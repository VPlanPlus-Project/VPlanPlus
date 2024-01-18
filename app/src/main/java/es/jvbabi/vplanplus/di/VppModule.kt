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
import es.jvbabi.vplanplus.data.repository.MessageRepositoryImpl
import es.jvbabi.vplanplus.data.repository.NotificationRepositoryImpl
import es.jvbabi.vplanplus.data.repository.PlanRepositoryImpl
import es.jvbabi.vplanplus.data.repository.ProfileRepositoryImpl
import es.jvbabi.vplanplus.data.repository.RoomRepositoryImpl
import es.jvbabi.vplanplus.data.repository.SchoolRepositoryImpl
import es.jvbabi.vplanplus.data.repository.TeacherRepositoryImpl
import es.jvbabi.vplanplus.data.repository.TimeRepositoryImpl
import es.jvbabi.vplanplus.data.repository.VPlanRepositoryImpl
import es.jvbabi.vplanplus.data.repository.WeekRepositoryImpl
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateTimeConverter
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
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.domain.usecase.ClassUseCases
import es.jvbabi.vplanplus.domain.usecase.KeyValueUseCases
import es.jvbabi.vplanplus.domain.usecase.LessonUseCases
import es.jvbabi.vplanplus.domain.usecase.ProfileUseCases
import es.jvbabi.vplanplus.domain.usecase.SchoolUseCases
import es.jvbabi.vplanplus.domain.usecase.VPlanUseCases
import es.jvbabi.vplanplus.domain.usecase.find_room.FindRoomUseCases
import es.jvbabi.vplanplus.domain.usecase.find_room.GetRoomMapUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentSchoolUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.general.data.IsSyncRunningUseCase
import es.jvbabi.vplanplus.domain.usecase.general.data.RunSyncUseCase
import es.jvbabi.vplanplus.domain.usecase.general.data.SyncUseCases
import es.jvbabi.vplanplus.domain.usecase.logs.DeleteAllLogsUseCase
import es.jvbabi.vplanplus.domain.usecase.logs.GetLogsUseCase
import es.jvbabi.vplanplus.domain.usecase.logs.LogsUseCases
import es.jvbabi.vplanplus.domain.usecase.onboarding.CheckSchoolIdSyntax
import es.jvbabi.vplanplus.domain.usecase.onboarding.DefaultLessonUseCase
import es.jvbabi.vplanplus.domain.usecase.onboarding.GetSchoolByIdUseCase
import es.jvbabi.vplanplus.domain.usecase.onboarding.LoginUseCase
import es.jvbabi.vplanplus.domain.usecase.onboarding.OnboardingUseCases
import es.jvbabi.vplanplus.domain.usecase.onboarding.ProfileOptionsUseCase
import es.jvbabi.vplanplus.domain.usecase.onboarding.SaveProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.onboarding.TestSchoolExistence
import es.jvbabi.vplanplus.domain.usecase.profile.GetLessonTimesForClassUseCase
import es.jvbabi.vplanplus.domain.usecase.profile.GetSchoolFromProfileUseCase
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
            .addMigrations(VppDatabase.migration_6_7)
            .addTypeConverter(LocalDateConverter())
            .addTypeConverter(LocalDateTimeConverter())
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
    fun provideMessageRepository(
        db: VppDatabase,
        @ApplicationContext context: Context,
        logRecordRepository: LogRecordRepository,
        notificationRepository: NotificationRepository
    ): MessageRepository {
        return MessageRepositoryImpl(
            messageDao = db.messageDao,
            context = context,
            logRecordRepository = logRecordRepository,
            notificationRepository = notificationRepository
        )
    }

    @Provides
    @Singleton
    fun provideProfileRepository(db: VppDatabase): ProfileRepository {
        return ProfileRepositoryImpl(db.profileDao, db.profileDefaultLessonsCrossoverDao)
    }

    @Provides
    @Singleton
    fun provideClassRepository(db: VppDatabase): ClassRepository {
        return ClassRepositoryImpl(db.schoolEntityDao)
    }

    @Provides
    @Singleton
    fun provideTimeRepository(): TimeRepository {
        return TimeRepositoryImpl()
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
        return TeacherRepositoryImpl(db.schoolEntityDao)
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
            lessonRepository = provideLessonRepository(db),
            planDao = db.planDao
        )
    }

    @Provides
    @Singleton
    fun provideRoomRepository(db: VppDatabase): RoomRepository {
        return RoomRepositoryImpl(db.schoolEntityDao)
    }

    @Provides
    @Singleton
    fun provideDefaultLessonRepository(db: VppDatabase): DefaultLessonRepository {
        return DefaultLessonRepositoryImpl(db.defaultLessonDao)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(@ApplicationContext context: Context, logRecordRepository: LogRecordRepository): NotificationRepository {
        return NotificationRepositoryImpl(context, logRecordRepository)
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
    fun provideProfileUseCases(
        repository: ProfileRepository,
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        calendarRepository: CalendarRepository
    ): ProfileUseCases {
        return ProfileUseCases(
            profileRepository = repository,
            keyValueRepository = keyValueRepository,
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository,
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
            lessonSchoolEntityCrossoverDao = db.lessonSchoolEntityCrossoverDao,
            keyValueUseCases = provideKeyValueUseCases(provideKeyValueRepository(db)),
            planRepository = providePlanRepository(db)
        )
    }

    @Provides
    @Singleton
    fun provideOnboardingUseCases(
        schoolRepository: SchoolRepository,
        baseDataRepository: BaseDataRepository,
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        vPlanRepository: VPlanRepository,
        profileRepository: ProfileRepository,
        defaultLessonRepository: DefaultLessonRepository,
        holidayRepository: HolidayRepository,
        lessonTimeRepository: LessonTimeRepository,
        @ApplicationContext context: Context
    ): OnboardingUseCases {
        return OnboardingUseCases(
            checkSchoolIdSyntax = CheckSchoolIdSyntax(schoolRepository),
            testSchoolExistence = TestSchoolExistence(schoolRepository),
            loginUseCase = LoginUseCase(schoolRepository, keyValueRepository, baseDataRepository),
            profileOptionsUseCase = ProfileOptionsUseCase(schoolRepository, classRepository, teacherRepository, roomRepository, keyValueRepository),
            defaultLessonUseCase = DefaultLessonUseCase(vPlanRepository, keyValueRepository),
            saveProfileUseCase = SaveProfileUseCase(
                schoolRepository = schoolRepository,
                kv = keyValueRepository,
                classRepository = classRepository,
                teacherRepository = teacherRepository,
                roomRepository = roomRepository,
                defaultLessonRepository = defaultLessonRepository,
                profileRepository = profileRepository,
                holidayRepository = holidayRepository,
                lessonTimeRepository = lessonTimeRepository,
                context = context
            ),
            getSchoolByIdUseCase = GetSchoolByIdUseCase(schoolRepository)
        )
    }

    @Singleton
    @Provides
    fun provideLogsUseCases(
        logRecordRepository: LogRecordRepository
    ): LogsUseCases {
        return LogsUseCases(
            getLogsUseCase = GetLogsUseCase(logRecordRepository),
            deleteAllLogsUseCase = DeleteAllLogsUseCase(logRecordRepository)
        )
    }

    @Singleton
    @Provides
    fun provideFindRoomUseCases(
        roomRepository: RoomRepository,
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        lessonTimeRepository: LessonTimeRepository,
        lessonUseCases: LessonUseCases
    ): FindRoomUseCases {
        return FindRoomUseCases(
            getRoomMapUseCase = GetRoomMapUseCase(
                roomRepository = roomRepository,
                keyValueRepository = keyValueRepository,
                lessonUseCases = lessonUseCases,
                lessonTimeRepository = lessonTimeRepository,
                classRepository = classRepository
            ),
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentProfileUseCase(
        profileRepository: ProfileRepository,
        keyValueRepository: KeyValueRepository
    ): GetCurrentProfileUseCase {
        return GetCurrentProfileUseCase(
            profileRepository = profileRepository,
            keyValueRepository = keyValueRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentSchoolUseCase(
        keyValueRepository: KeyValueRepository,
        profileRepository: ProfileRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository
    ): GetCurrentSchoolUseCase {
        return GetCurrentSchoolUseCase(
            keyValueRepository = keyValueRepository,
            profileRepository = profileRepository,
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetClassByProfileUseCase(classRepository: ClassRepository): GetClassByProfileUseCase {
        return GetClassByProfileUseCase(classRepository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentLessonNumberUseCase(lessonTimeRepository: LessonTimeRepository): GetCurrentLessonNumberUseCase {
        return GetCurrentLessonNumberUseCase(lessonTimeRepository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentTimeUseCase(): GetCurrentTimeUseCase {
        return GetCurrentTimeUseCase()
    }

    @Provides
    @Singleton
    fun provideGetSchoolFromProfileUseCase(
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository
    ): GetSchoolFromProfileUseCase {
        return GetSchoolFromProfileUseCase(
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository
        )
    }

    @Provides
    @Singleton
    fun provideSyncUseCases(@ApplicationContext context: Context): SyncUseCases {
        val isSyncRunningUseCase = IsSyncRunningUseCase(context)
        return SyncUseCases(
            runSyncUseCase = RunSyncUseCase(context, isSyncRunningUseCase),
            isSyncRunningUseCase = isSyncRunningUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetLessonTimesForClassUseCase(lessonTimeRepository: LessonTimeRepository): GetLessonTimesForClassUseCase {
        return GetLessonTimesForClassUseCase(lessonTimeRepository)
    }
}