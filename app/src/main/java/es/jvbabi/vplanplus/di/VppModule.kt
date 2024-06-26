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
import es.jvbabi.vplanplus.data.repository.DefaultLessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.FirebaseCloudMessagingManagerRepositoryImpl
import es.jvbabi.vplanplus.data.repository.GroupRepositoryImpl
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
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.data.source.database.converter.GradeModifierConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.UuidConverter
import es.jvbabi.vplanplus.data.source.database.converter.VppIdStateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.FirebaseCloudMessagingManagerRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
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
import es.jvbabi.vplanplus.domain.usecase.calendar.UpdateCalendarUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.general.SetBalloonUseCase
import es.jvbabi.vplanplus.domain.usecase.home.search.QueryUseCase
import es.jvbabi.vplanplus.domain.usecase.home.search.SearchUseCases
import es.jvbabi.vplanplus.domain.usecase.profile.GetLessonTimesForClassUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.GeneralSettingsUseCases
import es.jvbabi.vplanplus.domain.usecase.settings.general.GetColorsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.GetSettingsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.UpdateGradeProtectionUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.general.UpdateSettingsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.DeleteProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.DeleteSchoolUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetCalendarsUseCase
import es.jvbabi.vplanplus.domain.usecase.settings.profiles.GetProfilesUseCase
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
import es.jvbabi.vplanplus.domain.usecase.vpp_id.GetVppIdDetailsUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.UpdateMissingLinksStateUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.VppIdLinkUseCases
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_grades.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.CheckCredentialsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.UpdateCredentialsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.HasProfileLocalHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.GetProfilesWhichCanBeUsedForVppIdUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.SetProfileVppIdUseCase
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
            .addMigrations(VppDatabase.migration_22_23)
            .addMigrations(VppDatabase.migration_23_24)
            .addMigrations(VppDatabase.migration_24_25)
            .addMigrations(VppDatabase.migration_27_28)
            .addMigrations(VppDatabase.migration_28_29)
            .addMigrations(VppDatabase.migration_29_30)
            .addTypeConverter(LocalDateConverter())
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
            vppIdNetworkRepository = provideVppIdNetworkRepository(
                keyValueRepository = KeyValueRepositoryImpl(db.keyValueDao),
                logRecordRepository = logRecordRepository
            ),
            schoolDao = db.schoolDao,
            firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentTimeUseCase(timeRepository: TimeRepository) = GetCurrentTimeUseCase(timeRepository)

    @Provides
    @Singleton
    fun provideKeyValueRepository(db: VppDatabase): KeyValueRepository {
        return KeyValueRepositoryImpl(db.keyValueDao)
    }

    @Provides
    @Singleton
    fun provideProfileRepository(db: VppDatabase): ProfileRepository {
        return ProfileRepositoryImpl(
            profileDao = db.profileDao,
            profileDefaultLessonsCrossoverDao = db.profileDefaultLessonsCrossoverDao,
        )
    }

    @Provides
    @Singleton
    fun provideClassRepository(
        db: VppDatabase,
        keyValueRepository: KeyValueRepository,
        logRecordRepository: LogRecordRepository
    ): GroupRepository {
        return GroupRepositoryImpl(db.groupDao, provideVppIdNetworkRepository(keyValueRepository, logRecordRepository))
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
    fun provideLessonTimeRepository(db: VppDatabase): LessonTimeRepository {
        return LessonTimeRepositoryImpl(db.lessonTimeDao)
    }

    @Provides
    @Singleton
    fun provideBaseDataRepository(
        groupRepository: GroupRepository,
        lessonTimeRepository: LessonTimeRepository,
        holidayRepository: HolidayRepository,
        roomRepository: RoomRepository,
        teacherRepository: TeacherRepository,
        sp24NetworkRepository: Sp24NetworkRepository
    ): BaseDataRepository {
        return BaseDataRepositoryImpl(
            groupRepository,
            lessonTimeRepository,
            holidayRepository,
            roomRepository,
            teacherRepository,
            sp24NetworkRepository
        )
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
    fun provideLessonRepository(
        db: VppDatabase,
        profileRepository: ProfileRepository
    ): LessonRepository {
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
        holidayRepository: HolidayRepository,
        groupRepository: GroupRepository
    ): PlanRepository {
        return PlanRepositoryImpl(
            holidayRepository = holidayRepository,
            teacherRepository = provideTeacherRepository(db),
            groupRepository = groupRepository,
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
        groupRepository: GroupRepository,
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
            vppIdNetworkRepository = provideVppIdNetworkRepository(
                keyValueRepository,
                logRecordRepository
            ),
            groupRepository = groupRepository,
            profileRepository = profileRepository,
            notificationRepository = notificationRepository,
            stringRepository = stringRepository
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseCloudMessagingManagerRepository(
        groupRepository: GroupRepository,
        profileRepository: ProfileRepository,
        logRecordRepository: LogRecordRepository,
        keyValueRepository: KeyValueRepository,
        db: VppDatabase
    ): FirebaseCloudMessagingManagerRepository {
        return FirebaseCloudMessagingManagerRepositoryImpl(
            vppIdTokenDao = db.vppIdTokenDao,
            schoolEntityDao = db.schoolEntityDao,
            groupRepository = groupRepository,
            vppIdNetworkRepository = provideVppIdNetworkRepository(
                keyValueRepository,
                logRecordRepository
            ),
            logRecordRepository = logRecordRepository,
            keyValueRepository = keyValueRepository,
            profileRepository = profileRepository
        )
    }

    @Provides
    @Singleton
    fun provideDefaultLessonRepository(db: VppDatabase): DefaultLessonRepository {
        return DefaultLessonRepositoryImpl(db.defaultLessonDao)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        @ApplicationContext context: Context,
        logRecordRepository: LogRecordRepository
    ): NotificationRepository {
        return NotificationRepositoryImpl(context, logRecordRepository)
    }

    @Provides
    @Singleton
    fun provideVppIdRepository(
        db: VppDatabase,
        groupRepository: GroupRepository,
        profileRepository: ProfileRepository,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository,
        keyValueRepository: KeyValueRepository,
        logRecordRepository: LogRecordRepository
    ): VppIdRepository {
        return VppIdRepositoryImpl(
            vppIdDao = db.vppIdDao,
            vppIdTokenDao = db.vppIdTokenDao,
            groupRepository = groupRepository,
            roomBookingDao = db.roomBookingDao,
            vppIdNetworkRepository = provideVppIdNetworkRepository(
                keyValueRepository,
                logRecordRepository
            ),
            firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository,
            profileRepository = profileRepository
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
        defaultLessonRepository: DefaultLessonRepository,
        profileRepository: ProfileRepository
    ): ProfileDefaultLessonsUseCases {
        val changeDefaultLessonUseCase = ChangeDefaultLessonUseCase(profileRepository)
        return ProfileDefaultLessonsUseCases(
            getProfileByIdUseCase = getProfileByIdUseCase,
            isInconsistentStateUseCase = IsInconsistentStateUseCase(
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
    fun provideSearchUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        schoolRepository: SchoolRepository,
        groupRepository: GroupRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        lessonRepository: LessonRepository,
        keyValueRepository: KeyValueRepository
    ): SearchUseCases {
        return SearchUseCases(
            queryUseCase = QueryUseCase(
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                schoolRepository = schoolRepository,
                groupRepository = groupRepository,
                teacherRepository = teacherRepository,
                roomRepository = roomRepository,
                lessonRepository = lessonRepository,
                keyValueRepository = keyValueRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentIdentityUseCase(
        keyValueRepository: KeyValueRepository,
        profileRepository: ProfileRepository
    ): GetCurrentProfileUseCase {
        return GetCurrentProfileUseCase(
            keyValueRepository = keyValueRepository,
            profileRepository = profileRepository,
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentLessonNumberUseCase(lessonTimeRepository: LessonTimeRepository): GetCurrentLessonNumberUseCase {
        return GetCurrentLessonNumberUseCase(lessonTimeRepository)
    }

    @Provides
    @Singleton
    fun provideSyncUseCases(
        @ApplicationContext context: Context,
        doSyncUseCase: DoSyncUseCase
    ): SyncUseCases {
        val isSyncRunningUseCase = IsSyncRunningUseCase(context)
        return SyncUseCases(
            triggerSyncUseCase = TriggerSyncUseCase(context, isSyncRunningUseCase),
            isSyncRunningUseCase = isSyncRunningUseCase,
            doWorkUseCase = doSyncUseCase
        )
    }

    @Provides
    @Singleton
    fun provideDoSyncUseCase(
        @ApplicationContext context: Context,
        keyValueRepository: KeyValueRepository,
        logRecordRepository: LogRecordRepository,
        messageRepository: MessageRepository,
        schoolRepository: SchoolRepository,
        roomRepository: RoomRepository,
        groupRepository: GroupRepository,
        teacherRepository: TeacherRepository,
        defaultLessonRepository: DefaultLessonRepository,
        lessonTimeRepository: LessonTimeRepository,
        profileRepository: ProfileRepository,
        lessonRepository: LessonRepository,
        vPlanRepository: VPlanRepository,
        planRepository: PlanRepository,
        db: VppDatabase,
        systemRepository: SystemRepository,
        notificationRepository: NotificationRepository,
        gradeRepository: GradeRepository,
        homeworkRepository: HomeworkRepository,
        updateCalendarUseCase: UpdateCalendarUseCase
    ) = DoSyncUseCase(
        context = context,
        keyValueRepository = keyValueRepository,
        logRecordRepository = logRecordRepository,
        messageRepository = messageRepository,
        schoolRepository = schoolRepository,
        roomRepository = roomRepository,
        groupRepository = groupRepository,
        teacherRepository = teacherRepository,
        defaultLessonRepository = defaultLessonRepository,
        lessonTimesRepository = lessonTimeRepository,
        profileRepository = profileRepository,
        lessonRepository = lessonRepository,
        vPlanRepository = vPlanRepository,
        planRepository = planRepository,
        lessonSchoolEntityCrossoverDao = db.lessonSchoolEntityCrossoverDao,
        systemRepository = systemRepository,
        notificationRepository = notificationRepository,
        gradeRepository = gradeRepository,
        homeworkRepository = homeworkRepository,
        updateCalendarUseCase = updateCalendarUseCase
    )

    @Provides
    @Singleton
    fun provideGetLessonTimesForClassUseCase(lessonTimeRepository: LessonTimeRepository): GetLessonTimesForClassUseCase {
        return GetLessonTimesForClassUseCase(lessonTimeRepository)
    }

    @Provides
    @Singleton
    fun provideProfileSettingsUseCases(
        baseDataRepository: BaseDataRepository,
        profileRepository: ProfileRepository,
        schoolRepository: SchoolRepository,
        homeworkRepository: HomeworkRepository,
        keyValueRepository: KeyValueRepository,
        calendarRepository: CalendarRepository,
        notificationRepository: NotificationRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        updateCalendarUseCase: UpdateCalendarUseCase
    ): ProfileSettingsUseCases {
        return ProfileSettingsUseCases(
            getProfilesUseCase = GetProfilesUseCase(
                profileRepository = profileRepository,
            ),
            deleteSchoolUseCase = DeleteSchoolUseCase(
                schoolRepository = schoolRepository,
                profileRepository = profileRepository,
                keyValueRepository = keyValueRepository,
                notificationRepository = notificationRepository,
            ),
            getProfileByIdUseCase = GetProfileByIdUseCase(
                profileRepository = profileRepository
            ),
            getCalendarsUseCase = GetCalendarsUseCase(
                calendarRepository = calendarRepository
            ),
            updateCalendarTypeUseCase = UpdateCalendarTypeUseCase(
                profileRepository = profileRepository,
                updateCalendarUseCase = updateCalendarUseCase
            ),
            updateCalendarIdUseCase = UpdateCalendarIdUseCase(
                profileRepository = profileRepository,
                updateCalendarUseCase = updateCalendarUseCase
            ),
            updateProfileDisplayNameUseCase = UpdateProfileDisplayNameUseCase(
                profileRepository = profileRepository
            ),
            deleteProfileUseCase = DeleteProfileUseCase(
                profileRepository = profileRepository,
                schoolRepository = schoolRepository,
                keyValueRepository = keyValueRepository,
                getCurrentProfileUseCase = getCurrentProfileUseCase,
                notificationRepository = notificationRepository,
                updateCalendarUseCase = updateCalendarUseCase
            ),
            checkCredentialsUseCase = CheckCredentialsUseCase(baseDataRepository),
            updateCredentialsUseCase = UpdateCredentialsUseCase(schoolRepository, notificationRepository),
            hasProfileLocalHomeworkUseCase = HasProfileLocalHomeworkUseCase(homeworkRepository),
            updateHomeworkEnabledUseCase = UpdateHomeworkEnabledUseCase(profileRepository, homeworkRepository)
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
    ): GetProfilesUseCase {
        return GetProfilesUseCase(
            profileRepository = profileRepository,
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
        profileRepository: ProfileRepository,
        keyValueRepository: KeyValueRepository
    ): VppIdLinkUseCases {
        val testForMissingVppIdToProfileConnectionsUseCase = TestForMissingVppIdToProfileConnectionsUseCase(vppIdRepository, profileRepository)
        return VppIdLinkUseCases(
            getVppIdDetailsUseCase = GetVppIdDetailsUseCase(vppIdRepository, SetBalloonUseCase(keyValueRepository)),
            getProfilesWhichCanBeUsedForVppIdUseCase = GetProfilesWhichCanBeUsedForVppIdUseCase(profileRepository),
            setProfileVppIdUseCase = SetProfileVppIdUseCase(profileRepository, keyValueRepository, testForMissingVppIdToProfileConnectionsUseCase),
            updateMissingLinksStateUseCase = UpdateMissingLinksStateUseCase(
                keyValueRepository = keyValueRepository,
                testForMissingVppIdToProfileConnectionsUseCase = testForMissingVppIdToProfileConnectionsUseCase
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