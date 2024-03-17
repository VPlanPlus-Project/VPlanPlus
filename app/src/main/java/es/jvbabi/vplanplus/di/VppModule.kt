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
import es.jvbabi.vplanplus.data.repository.AlarmManagerRepositoryImpl
import es.jvbabi.vplanplus.data.repository.BaseDataRepositoryImpl
import es.jvbabi.vplanplus.data.repository.BiometricRepositoryImpl
import es.jvbabi.vplanplus.data.repository.CalendarRepositoryImpl
import es.jvbabi.vplanplus.data.repository.ClassRepositoryImpl
import es.jvbabi.vplanplus.data.repository.DefaultLessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.FirebaseCloudMessagingManagerRepositoryImpl
import es.jvbabi.vplanplus.data.repository.HolidayRepositoryImpl
import es.jvbabi.vplanplus.data.repository.LessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.LessonTimeRepositoryImpl
import es.jvbabi.vplanplus.data.repository.NotificationRepositoryImpl
import es.jvbabi.vplanplus.data.repository.PlanRepositoryImpl
import es.jvbabi.vplanplus.data.repository.ProfileRepositoryImpl
import es.jvbabi.vplanplus.data.repository.RoomRepositoryImpl
import es.jvbabi.vplanplus.data.repository.SystemRepositoryImpl
import es.jvbabi.vplanplus.data.repository.TeacherRepositoryImpl
import es.jvbabi.vplanplus.data.repository.TimeRepositoryImpl
import es.jvbabi.vplanplus.data.repository.VppIdRepositoryImpl
import es.jvbabi.vplanplus.data.repository.WeekRepositoryImpl
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.data.source.database.converter.GradeModifierConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.UuidConverter
import es.jvbabi.vplanplus.data.source.database.converter.VppIdStateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.domain.usecase.find_room.BookRoomUseCase
import es.jvbabi.vplanplus.domain.usecase.find_room.CanBookRoomUseCase
import es.jvbabi.vplanplus.domain.usecase.find_room.CancelBookingUseCase
import es.jvbabi.vplanplus.domain.usecase.find_room.FindRoomUseCases
import es.jvbabi.vplanplus.domain.usecase.find_room.GetRoomMapUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetClassByProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentSchoolUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.home.SetUpUseCase
import es.jvbabi.vplanplus.domain.usecase.home.search.QueryUseCase
import es.jvbabi.vplanplus.domain.usecase.home.search.SearchUseCases
import es.jvbabi.vplanplus.domain.usecase.profile.GetLessonTimesForClassUseCase
import es.jvbabi.vplanplus.domain.usecase.profile.GetSchoolFromProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.advanced.AdvancedSettingsUseCases
import es.jvbabi.vplanplus.domain.usecase.settings.advanced.DeleteCacheUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.advanced.SetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.GeneralSettingsUseCases
import es.jvbabi.vplanplus.domain.usecase.settings.general.GetColorsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.GetSettingsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.UpdateGradeProtectionUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.UpdateSettingsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.DeleteProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.DeleteSchoolUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetCalendarsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetVppIdByClassUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.ProfileSettingsUseCases
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.UpdateCalendarIdUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.UpdateCalendarTypeUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.UpdateProfileDisplayNameUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons.ChangeDefaultLessonUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons.FixDefaultLessonsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons.IsInconsistentStateUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.lessons.ProfileDefaultLessonsUseCases
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.shared.GetProfileByIdUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.DoSyncUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.SyncUseCases
import es.jvbabi.vplanplus.domain.usecase.sync.TriggerSyncUseCase
import es.jvbabi.vplanplus.feature.main_timetable.domain.usecase.GetDataUseCase
import es.jvbabi.vplanplus.feature.main_timetable.domain.usecase.TimetableUseCases
import es.jvbabi.vplanplus.domain.usecase.vpp_id.GetVppIdDetailsUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.VppIdLinkUseCases
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.AccountSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.CloseSessionUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.DeleteAccountUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.GetAccountsUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.GetSessionsUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase.TestAccountUseCase
import es.jvbabi.vplanplus.shared.data.KeyValueRepositoryImpl
import es.jvbabi.vplanplus.shared.data.SchoolRepositoryImpl
import es.jvbabi.vplanplus.shared.data.Sp24NetworkRepository
import es.jvbabi.vplanplus.shared.data.StringRepositoryImpl
import es.jvbabi.vplanplus.shared.data.VPlanRepositoryImpl
import es.jvbabi.vplanplus.shared.data.VppIdNetworkRepository
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
            .addMigrations(VppDatabase.migration_7_8)
            .addMigrations(VppDatabase.migration_10_11)
            .addMigrations(VppDatabase.migration_11_12)
            .addMigrations(VppDatabase.migration_12_13)
            .addMigrations(VppDatabase.migration_20_21)
            .addTypeConverter(LocalDateConverter())
            .addTypeConverter(ProfileTypeConverter())
            .addTypeConverter(UuidConverter())
            .addTypeConverter(ProfileCalendarTypeConverter())
            .addTypeConverter(VppIdStateConverter())
            .addTypeConverter(GradeModifierConverter())
            .addTypeConverter(ZonedDateTimeConverter())
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .enableMultiInstanceInvalidation()
            .build()
    }

    @Provides
    @Singleton
    fun provideStringRepository(@ApplicationContext context: Context): StringRepository {
        return StringRepositoryImpl(context)
    }

    @Provides
    fun provideSP24NetworkRepository(
        logRecordRepository: LogRecordRepository?
    ): Sp24NetworkRepository {
        return Sp24NetworkRepository(logRepository = logRecordRepository)
    }

    @Provides
    @Singleton
    fun provideVppIdNetworkRepository(
        keyValueRepository: KeyValueRepository,
        logRecordRepository: LogRecordRepository?
    ): VppIdNetworkRepository {
        return VppIdNetworkRepository(
            keyValueRepository = keyValueRepository,
            logRepository = logRecordRepository
        )
    }

    // Repositories
    @Provides
    @Singleton
    fun provideSchoolRepository(
        db: VppDatabase,
        logRecordRepository: LogRecordRepository,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
    ): SchoolRepository {
        return SchoolRepositoryImpl(
            sp24NetworkRepository = provideSP24NetworkRepository(logRecordRepository = logRecordRepository),
            schoolDao = db.schoolDao,
            firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository
        )
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
    fun provideKeyValueRepository(db: VppDatabase): KeyValueRepository {
        return KeyValueRepositoryImpl(db.keyValueDao)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        db: VppDatabase,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository,
    ): ProfileRepository {
        return ProfileRepositoryImpl(
            profileDao = db.profileDao,
            schoolEntityDao = db.schoolEntityDao,
            keyValueDao = db.keyValueDao,
            profileDefaultLessonsCrossoverDao = db.profileDefaultLessonsCrossoverDao,
            firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository
        )
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
    fun provideHolidayRepository(
        db: VppDatabase,
        schoolRepository: SchoolRepository
    ): HolidayRepository {
        return HolidayRepositoryImpl(
            holidayDao = db.holidayDao,
            schoolRepository = schoolRepository
        )
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
        sp24NetworkRepository: Sp24NetworkRepository
    ): BaseDataRepository {
        return BaseDataRepositoryImpl(classRepository, lessonTimeRepository, holidayRepository, weekRepository, roomRepository, teacherRepository, sp24NetworkRepository)
    }

    @Provides
    @Singleton
    fun provideVPlanRepository(
        sp24NetworkRepository: Sp24NetworkRepository
    ): VPlanRepository {
        return VPlanRepositoryImpl(sp24NetworkRepository)
    }

    @Provides
    @Singleton
    fun provideTeacherRepository(db: VppDatabase): TeacherRepository {
        return TeacherRepositoryImpl(db.schoolEntityDao)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideLessonRepository(db: VppDatabase, profileRepository: ProfileRepository): LessonRepository {
        return LessonRepositoryImpl(
            lessonDao = db.lessonDao,
            profileRepository = profileRepository
        )
    }

    @Provides
    @Singleton
    fun providePlanRepository(
        db: VppDatabase,
        roomRepository: RoomRepository,
        profileRepository: ProfileRepository,
        holidayRepository: HolidayRepository
    ): PlanRepository {
        return PlanRepositoryImpl(
            holidayRepository = holidayRepository,
            teacherRepository = provideTeacherRepository(db),
            classRepository = provideClassRepository(db),
            roomRepository = roomRepository,
            lessonRepository = provideLessonRepository(db, profileRepository),
            planDao = db.planDao
        )
    }

    @Provides
    @Singleton
    fun provideRoomRepository(
        db: VppDatabase,
        vppIdRepository: VppIdRepository,
        classRepository: ClassRepository,
        logRecordRepository: LogRecordRepository,
        profileRepository: ProfileRepository,
        notificationRepository: NotificationRepository,
        stringRepository: StringRepository,
        keyValueRepository: KeyValueRepository
    ): RoomRepository {
        return RoomRepositoryImpl(
            schoolEntityDao = db.schoolEntityDao,
            roomBookingDao = db.roomBookingDao,
            vppIdRepository = vppIdRepository,
            vppIdNetworkRepository = provideVppIdNetworkRepository(keyValueRepository, logRecordRepository),
            classRepository = classRepository,
            profileRepository = profileRepository,
            notificationRepository = notificationRepository,
            stringRepository = stringRepository
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseCloudMessagingManagerRepository(
        classRepository: ClassRepository,
        logRecordRepository: LogRecordRepository,
        keyValueRepository: KeyValueRepository,
        db: VppDatabase
    ): FirebaseCloudMessagingManagerRepository {
        return FirebaseCloudMessagingManagerRepositoryImpl(
            profileDao = db.profileDao,
            vppIdDao = db.vppIdDao,
            vppIdTokenDao = db.vppIdTokenDao,
            schoolEntityDao = db.schoolEntityDao,
            classRepository = classRepository,
            vppIdNetworkRepository = provideVppIdNetworkRepository(keyValueRepository, logRecordRepository),
            logRecordRepository = logRecordRepository,
            keyValueRepository = keyValueRepository,
        )
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

    @Provides
    @Singleton
    fun provideVppIdRepository(
        db: VppDatabase,
        classRepository: ClassRepository,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository,
        keyValueRepository: KeyValueRepository,
        logRecordRepository: LogRecordRepository
    ): VppIdRepository {
        return VppIdRepositoryImpl(
            vppIdDao = db.vppIdDao,
            vppIdTokenDao = db.vppIdTokenDao,
            classRepository = classRepository,
            roomBookingDao = db.roomBookingDao,
            vppIdNetworkRepository = provideVppIdNetworkRepository(keyValueRepository, logRecordRepository),
            firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository
        )
    }

    @Provides
    @Singleton
    fun provideSystemRepository(): SystemRepository {
        return SystemRepositoryImpl()
    }

    // Use cases
    @Provides
    @Singleton
    fun provideProfileDefaultLessonsUseCases(
        getProfileByIdUseCase: GetProfileByIdUseCase,
        getClassByProfileUseCase: GetClassByProfileUseCase,
        defaultLessonRepository: DefaultLessonRepository,
        profileRepository: ProfileRepository
    ): ProfileDefaultLessonsUseCases {
        val changeDefaultLessonUseCase = ChangeDefaultLessonUseCase(profileRepository)
        return ProfileDefaultLessonsUseCases(
            getProfileByIdUseCase = getProfileByIdUseCase,
            isInconsistentStateUseCase = IsInconsistentStateUseCase(
                getClassByProfileUseCase = getClassByProfileUseCase,
                defaultLessonRepository = defaultLessonRepository
            ),
            changeDefaultLessonUseCase = changeDefaultLessonUseCase,
            fixDefaultLessonsUseCase = FixDefaultLessonsUseCase(
                changeDefaultLessonUseCase = changeDefaultLessonUseCase,
                defaultLessonRepository = defaultLessonRepository,
                profileRepository = profileRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideAccountSettingsUseCases(
        vppIdRepository: VppIdRepository
    ): AccountSettingsUseCases {
        return AccountSettingsUseCases(
            getAccountsUseCase = GetAccountsUseCase(vppIdRepository = vppIdRepository),
            testAccountUseCase = TestAccountUseCase(vppIdRepository = vppIdRepository),
            deleteAccountUseCase = DeleteAccountUseCase(vppIdRepository = vppIdRepository),
            getSessionsUseCase = GetSessionsUseCase(vppIdRepository = vppIdRepository),
            closeSessionUseCase = CloseSessionUseCase(vppIdRepository = vppIdRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSearchUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        schoolRepository: SchoolRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        lessonRepository: LessonRepository,
        keyValueRepository: KeyValueRepository
    ): SearchUseCases {
        return SearchUseCases(
            queryUseCase = QueryUseCase(
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                schoolRepository = schoolRepository,
                classRepository = classRepository,
                teacherRepository = teacherRepository,
                roomRepository = roomRepository,
                lessonRepository = lessonRepository,
                keyValueRepository = keyValueRepository
            )
        )
    }

    @Singleton
    @Provides
    fun provideFindRoomUseCases(
        roomRepository: RoomRepository,
        keyValueRepository: KeyValueRepository,
        classRepository: ClassRepository,
        lessonTimeRepository: LessonTimeRepository,
        vppIdRepository: VppIdRepository,
        planRepository: PlanRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ): FindRoomUseCases {
        return FindRoomUseCases(
            getRoomMapUseCase = GetRoomMapUseCase(
                roomRepository = roomRepository,
                keyValueRepository = keyValueRepository,
                planRepository = planRepository,
                lessonTimeRepository = lessonTimeRepository,
                classRepository = classRepository
            ),
            canBookRoomUseCase = CanBookRoomUseCase(
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                classRepository = classRepository,
                vppIdRepository = vppIdRepository,
            ),
            bookRoomUseCase = BookRoomUseCase(
                vppIdRepository = vppIdRepository,
                classRepository = classRepository,
                roomRepository = roomRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase,
            ),
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
            cancelBooking = CancelBookingUseCase(vppIdRepository)
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
    fun provideGetCurrentIdentityUseCase(
        vppIdRepository: VppIdRepository,
        classRepository: ClassRepository,
        keyValueRepository: KeyValueRepository,
        profileRepository: ProfileRepository
    ): GetCurrentIdentityUseCase {
        return GetCurrentIdentityUseCase(
            vppIdRepository = vppIdRepository,
            classRepository = classRepository,
            keyValueRepository = keyValueRepository,
            profileRepository = profileRepository,
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
    fun provideSyncUseCases(
        @ApplicationContext context: Context,
        keyValueRepository: KeyValueRepository,
        logRecordRepository: LogRecordRepository,
        messageRepository: MessageRepository,
        schoolRepository: SchoolRepository,
        roomRepository: RoomRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        defaultLessonRepository: DefaultLessonRepository,
        lessonTimeRepository: LessonTimeRepository,
        profileRepository: ProfileRepository,
        lessonRepository: LessonRepository,
        vPlanRepository: VPlanRepository,
        planRepository: PlanRepository,
        db: VppDatabase,
        systemRepository: SystemRepository,
        calendarRepository: CalendarRepository,
        getSchoolFromProfileUseCase: GetSchoolFromProfileUseCase,
        notificationRepository: NotificationRepository,
        gradeRepository: GradeRepository,
        homeworkRepository: HomeworkRepository
    ): SyncUseCases {
        val isSyncRunningUseCase = IsSyncRunningUseCase(context)
        return SyncUseCases(
            triggerSyncUseCase = TriggerSyncUseCase(context, isSyncRunningUseCase),
            isSyncRunningUseCase = isSyncRunningUseCase,
            doWorkUseCase = DoSyncUseCase(
                context = context,
                keyValueRepository = keyValueRepository,
                logRecordRepository = logRecordRepository,
                messageRepository = messageRepository,
                schoolRepository = schoolRepository,
                roomRepository = roomRepository,
                classRepository = classRepository,
                teacherRepository = teacherRepository,
                defaultLessonRepository = defaultLessonRepository,
                lessonTimesRepository = lessonTimeRepository,
                profileRepository = profileRepository,
                lessonRepository = lessonRepository,
                vPlanRepository = vPlanRepository,
                planRepository = planRepository,
                lessonSchoolEntityCrossoverDao = db.lessonSchoolEntityCrossoverDao,
                systemRepository = systemRepository,
                calendarRepository = calendarRepository,
                getSchoolFromProfileUseCase = getSchoolFromProfileUseCase,
                notificationRepository = notificationRepository,
                gradeRepository = gradeRepository,
                homeworkRepository = homeworkRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideGetLessonTimesForClassUseCase(lessonTimeRepository: LessonTimeRepository): GetLessonTimesForClassUseCase {
        return GetLessonTimesForClassUseCase(lessonTimeRepository)
    }
    
    @Provides
    @Singleton
    fun provideAdvancedSettingsUseCases(
        lessonRepository: LessonRepository,
        roomRepository: RoomRepository,
        gradeRepository: GradeRepository,
        homeworkRepository: HomeworkRepository,
        keyValueRepository: KeyValueRepository,
        systemRepository: SystemRepository
    ): AdvancedSettingsUseCases {
        return AdvancedSettingsUseCases(
            deleteCacheUseCase = DeleteCacheUseCase(
                lessonRepository,
                roomRepository,
                gradeRepository,
                homeworkRepository
            ),
            getVppIdServerUseCase = GetVppIdServerUseCase(keyValueRepository),
            setVppIdServerUseCase = SetVppIdServerUseCase(keyValueRepository, systemRepository)
        )
    }

    @Provides
    @Singleton
    fun provideProfileSettingsUseCases(
        profileRepository: ProfileRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        schoolRepository: SchoolRepository,
        keyValueRepository: KeyValueRepository,
        calendarRepository: CalendarRepository,
        vppIdRepository: VppIdRepository,
        notificationRepository: NotificationRepository,
        getSchoolFromProfileUseCase: GetSchoolFromProfileUseCase,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ): ProfileSettingsUseCases {
        return ProfileSettingsUseCases(
            getProfilesUseCase = GetProfilesUseCase(
                profileRepository = profileRepository,
                classRepository = classRepository,
                teacherRepository = teacherRepository,
                roomRepository = roomRepository
            ),
            deleteSchoolUseCase = DeleteSchoolUseCase(
                schoolRepository = schoolRepository,
                profileRepository = profileRepository,
                keyValueRepository = keyValueRepository,
                notificationRepository = notificationRepository,
                getSchoolFromProfileUseCase = getSchoolFromProfileUseCase
            ),
            getProfileByIdUseCase = GetProfileByIdUseCase(
                profileRepository = profileRepository
            ),
            getCalendarsUseCase = GetCalendarsUseCase(
                calendarRepository = calendarRepository
            ),
            updateCalendarTypeUseCase = UpdateCalendarTypeUseCase(
                profileRepository = profileRepository
            ),
            updateCalendarIdUseCase = UpdateCalendarIdUseCase(
                profileRepository = profileRepository
            ),
            updateProfileDisplayNameUseCase = UpdateProfileDisplayNameUseCase(
                profileRepository = profileRepository
            ),
            deleteProfileUseCase = DeleteProfileUseCase(
                profileRepository = profileRepository,
                schoolRepository = schoolRepository,
                keyValueRepository = keyValueRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase,
                notificationRepository = notificationRepository
            ),
            getVppIdByClassUseCase = GetVppIdByClassUseCase(
                vppIdRepository = vppIdRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideGetProfileByIdUseCase(
        profileRepository: ProfileRepository
    ): GetProfileByIdUseCase {
        return GetProfileByIdUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideGetProfilesUseCase(
        profileRepository: ProfileRepository,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository
    ): GetProfilesUseCase {
        return GetProfilesUseCase(
            profileRepository = profileRepository,
            classRepository = classRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository
        )
    }

    @Provides
    @Singleton
    fun provideSetUpUseCase(
        keyValueRepository: KeyValueRepository,
        homeworkRepository: HomeworkRepository,
        alarmManagerRepository: AlarmManagerRepository,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
    ): SetUpUseCase {
        return SetUpUseCase(
            keyValueRepository = keyValueRepository,
            homeworkRepository = homeworkRepository,
            alarmManagerRepository = alarmManagerRepository,
            firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository
        )
    }

    @Provides
    @Singleton
    fun provideGeneralSettingsUseCases(
        keyValueRepository: KeyValueRepository,
        biometricRepository: BiometricRepository,
        stringRepository: StringRepository
    ): GeneralSettingsUseCases {
        val getColorsUseCase = GetColorsUseCase(keyValueRepository)
        return GeneralSettingsUseCases(
            getColorsUseCase = getColorsUseCase,
            getSettingsUseCase = GetSettingsUseCase(
                keyValueRepository = keyValueRepository,
                getColorsUseCase = getColorsUseCase
            ),
            updateSettingsUseCase = UpdateSettingsUseCase(keyValueRepository),
            updateGradeProtectionUseCase = UpdateGradeProtectionUseCase(
                keyValueRepository = keyValueRepository,
                biometricRepository = biometricRepository,
                stringRepository = stringRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideVppIdLinkUseCases(
        vppIdRepository: VppIdRepository,
        classRepository: ClassRepository,
        gradeRepository: GradeRepository
    ): VppIdLinkUseCases {
        return VppIdLinkUseCases(
            getVppIdDetailsUseCase = GetVppIdDetailsUseCase(
                vppIdRepository = vppIdRepository,
                classRepository = classRepository,
                gradeRepository = gradeRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideTimetableUseCases(
        keyValueRepository: KeyValueRepository,
        planRepository: PlanRepository,
        getActiveProfileUseCase: GetCurrentProfileUseCase
    ): TimetableUseCases {
        return TimetableUseCases(
            getDataUseCase = GetDataUseCase(
                keyValueRepository = keyValueRepository,
                planRepository = planRepository,
                getActiveProfileUseCase = getActiveProfileUseCase
            )
        )
    }

    @Provides
    @Singleton
    fun provideBiometricRepository(@ApplicationContext context: Context): BiometricRepository {
        return BiometricRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideAlarmManagerRepository(@ApplicationContext context: Context): AlarmManagerRepository {
        return AlarmManagerRepositoryImpl(context)
    }
}