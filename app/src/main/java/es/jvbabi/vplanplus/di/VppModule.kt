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
import es.jvbabi.vplanplus.data.repository.DailyReminderRepositoryImpl
import es.jvbabi.vplanplus.data.repository.DefaultLessonRepositoryImpl
import es.jvbabi.vplanplus.data.repository.FileRepositoryImpl
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
import es.jvbabi.vplanplus.data.repository.TimetableRepositoryImpl
import es.jvbabi.vplanplus.data.repository.VppIdRepositoryImpl
import es.jvbabi.vplanplus.data.repository.WeekRepositoryImpl
import es.jvbabi.vplanplus.data.source.database.VppDatabase
import es.jvbabi.vplanplus.data.source.database.converter.GradeModifierConverter
import es.jvbabi.vplanplus.data.source.database.converter.LocalDateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ProfileCalendarTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.SchoolDownloadTypeConverter
import es.jvbabi.vplanplus.data.source.database.converter.UuidConverter
import es.jvbabi.vplanplus.data.source.database.converter.VppIdStateConverter
import es.jvbabi.vplanplus.data.source.database.converter.ZonedDateTimeConverter
import es.jvbabi.vplanplus.domain.repository.AlarmManagerRepository
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.CalendarRepository
import es.jvbabi.vplanplus.domain.repository.DailyReminderRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.FileRepository
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
import es.jvbabi.vplanplus.domain.repository.TimetableRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.repository.WeekRepository
import es.jvbabi.vplanplus.domain.usecase.calendar.UpdateCalendarUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentLessonNumberUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetDayUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetDefaultLessonByIdentifierUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetNextDayUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.general.IsDeveloperModeEnabledUseCase
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
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.GetVppIdDetailsUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.TestForMissingVppIdToProfileConnectionsUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.UpdateMissingLinksStateUseCase
import es.jvbabi.vplanplus.domain.usecase.vpp_id.VppIdLinkUseCases
import es.jvbabi.vplanplus.feature.exams.domain.repository.ExamRepository
import es.jvbabi.vplanplus.feature.exams.domain.usecase.UpdateAssessmentsUseCase
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.main_grades.common.domain.usecases.UpdateGradesUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.UpdateHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.UpdateFcmTokenUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.CheckCredentialsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.UpdateCredentialsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.HasProfileLocalAssessmentsUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.HasProfileLocalHomeworkUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateAssessmentsEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.profile.domain.usecase.profile.UpdateHomeworkEnabledUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.GetProfilesWhichCanBeUsedForVppIdUseCase
import es.jvbabi.vplanplus.feature.settings.vpp_id.domain.usecase.SetProfileVppIdUseCase
import es.jvbabi.vplanplus.shared.data.BsNetworkRepository
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
            .addMigrations(VppDatabase.migration_37_38)
            .addMigrations(VppDatabase.migration_38_39)
            .addMigrations(VppDatabase.migration_40_41)
            .addMigrations(VppDatabase.migration_42_43)
            .addTypeConverter(LocalDateConverter())
            .addTypeConverter(UuidConverter())
            .addTypeConverter(ProfileCalendarTypeConverter())
            .addTypeConverter(VppIdStateConverter())
            .addTypeConverter(GradeModifierConverter())
            .addTypeConverter(ZonedDateTimeConverter())
            .addTypeConverter(SchoolDownloadTypeConverter())
            .fallbackToDestructiveMigration()
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .enableMultiInstanceInvalidation()
            .build()
    }

    @Provides
    @Singleton
    fun provideFileRepository(@ApplicationContext context: Context): FileRepository {
        return FileRepositoryImpl(context)
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
        keyValueRepository: KeyValueRepository,
        updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase
    ): SchoolRepository {
        return SchoolRepositoryImpl(
            sp24NetworkRepository = provideSP24NetworkRepository(logRecordRepository = logRecordRepository),
            vppIdNetworkRepository = provideVppIdNetworkRepository(
                keyValueRepository = KeyValueRepositoryImpl(db.keyValueDao),
                logRecordRepository = logRecordRepository
            ),
            schoolDao = db.schoolDao,
            updateFcmTokenUseCase = UpdateFcmTokenUseCase(keyValueRepository, updateFirebaseTokenUseCase)
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
        return GroupRepositoryImpl(
            groupDao = db.groupDao,
            schoolDao = db.schoolDao,
            vppIdNetworkRepository = provideVppIdNetworkRepository(keyValueRepository, logRecordRepository)
        )
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
        sp24NetworkRepository: Sp24NetworkRepository
    ): BaseDataRepository {
        return BaseDataRepositoryImpl(
            sp24NetworkRepository
        )
    }

    @Provides
    @Singleton
    fun provideVPlanRepository(
        sp24NetworkRepository: Sp24NetworkRepository,
        db: VppDatabase
    ): VPlanRepository {
        return VPlanRepositoryImpl(sp24NetworkRepository, db.sPlanInWeekDao)
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
    fun providePlanRepository(
        db: VppDatabase,
        roomRepository: RoomRepository,
        holidayRepository: HolidayRepository,
        groupRepository: GroupRepository
    ): PlanRepository {
        return PlanRepositoryImpl(
            holidayRepository = holidayRepository,
            teacherRepository = provideTeacherRepository(db),
            groupRepository = groupRepository,
            roomRepository = roomRepository,
            lessonRepository = provideLessonRepository(db),
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
            roomDao = db.roomDao,
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
        logRecordRepository: LogRecordRepository,
        keyValueRepository: KeyValueRepository,
    ): FirebaseCloudMessagingManagerRepository {
        return FirebaseCloudMessagingManagerRepositoryImpl(
            vppIdNetworkRepository = provideVppIdNetworkRepository(
                keyValueRepository,
                logRecordRepository
            ),
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
        keyValueRepository: KeyValueRepository,
        logRecordRepository: LogRecordRepository,
        schulverwalterNetworkRepository: BsNetworkRepository
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
            profileRepository = profileRepository,
            schulverwalterNetworkRepository = schulverwalterNetworkRepository
        )
    }

    @Provides
    @Singleton
    fun provideSystemRepository(
        @ApplicationContext context: Context
    ): SystemRepository {
        return SystemRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideDailyReminderRepository(
        keyValueRepository: KeyValueRepository
    ): DailyReminderRepository {
        return DailyReminderRepositoryImpl(keyValueRepository)
    }

    // Use cases
    @Provides
    @Singleton
    fun provideUpdateFirebaseTokenUseCase(
        profileRepository: ProfileRepository,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
    ): UpdateFirebaseTokenUseCase {
        return UpdateFirebaseTokenUseCase(profileRepository, firebaseCloudMessagingManagerRepository)
    }

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
        timetableRepository: TimetableRepository,
        holidayRepository: HolidayRepository,
        baseDataRepository: BaseDataRepository,
        weekRepository: WeekRepository,
        updateCalendarUseCase: UpdateCalendarUseCase,
        updateHomeworkUseCase: UpdateHomeworkUseCase,
        updateGradesUseCase: UpdateGradesUseCase,
        updateAssessmentsUseCase: UpdateAssessmentsUseCase,
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
        updateCalendarUseCase = updateCalendarUseCase,
        updateHomeworkUseCase = updateHomeworkUseCase,
        updateGradesUseCase = updateGradesUseCase,
        updateAssessmentsUseCase = updateAssessmentsUseCase,
        weekRepository = weekRepository,
        timetableRepository = timetableRepository,
        holidayRepository = holidayRepository,
        baseDataRepository = baseDataRepository
    )

    @Provides
    @Singleton
    fun provideGetLessonTimesForClassUseCase(lessonTimeRepository: LessonTimeRepository): GetLessonTimesForClassUseCase {
        return GetLessonTimesForClassUseCase(lessonTimeRepository)
    }

    @Provides
    @Singleton
    fun provideGetDayUseCase(
        timetableRepository: TimetableRepository,
        examRepository: ExamRepository,
        holidayRepository: HolidayRepository,
        lessonRepository: LessonRepository,
        homeworkRepository: HomeworkRepository,
        keyValueRepository: KeyValueRepository,
        planRepository: PlanRepository,
        gradeRepository: GradeRepository
    ) = GetDayUseCase(
        timetableRepository = timetableRepository,
        examRepository = examRepository,
        holidayRepository = holidayRepository,
        lessonRepository = lessonRepository,
        homeworkRepository = homeworkRepository,
        keyValueRepository = keyValueRepository,
        planRepository = planRepository,
        gradeRepository = gradeRepository
    )

    @Provides
    @Singleton
    fun provideGetNextDayUseCase(
        planRepository: PlanRepository,
        holidayRepository: HolidayRepository,
        getDayUseCase: GetDayUseCase
    ) = GetNextDayUseCase(
        planRepository = planRepository,
        holidayRepository = holidayRepository,
        getDayUseCase = getDayUseCase
    )

    @Provides
    @Singleton
    fun provideProfileSettingsUseCases(
        baseDataRepository: BaseDataRepository,
        profileRepository: ProfileRepository,
        schoolRepository: SchoolRepository,
        homeworkRepository: HomeworkRepository,
        keyValueRepository: KeyValueRepository,
        calendarRepository: CalendarRepository,
        examRepository: ExamRepository,
        notificationRepository: NotificationRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        updateCalendarUseCase: UpdateCalendarUseCase,
        updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase
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
                updateCalendarUseCase = updateCalendarUseCase,
                updateFirebaseTokenUseCase = updateFirebaseTokenUseCase
            ),
            checkCredentialsUseCase = CheckCredentialsUseCase(baseDataRepository),
            updateCredentialsUseCase = UpdateCredentialsUseCase(schoolRepository, notificationRepository),
            hasProfileLocalHomeworkUseCase = HasProfileLocalHomeworkUseCase(homeworkRepository, getCurrentProfileUseCase),
            updateHomeworkEnabledUseCase = UpdateHomeworkEnabledUseCase(profileRepository, homeworkRepository),

            hasProfileLocalAssessmentsUseCase = HasProfileLocalAssessmentsUseCase(examRepository, getCurrentProfileUseCase),
            updateAssessmentsEnabledUseCase = UpdateAssessmentsEnabledUseCase(profileRepository, examRepository)
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentServerUseCase(keyValueRepository: KeyValueRepository) = GetVppIdServerUseCase(keyValueRepository)

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
    fun provideIsDeveloperModeEnabledUseCase(
        keyValueRepository: KeyValueRepository
    ): IsDeveloperModeEnabledUseCase {
        return IsDeveloperModeEnabledUseCase(keyValueRepository)
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
    fun provideGetDefaultLessonByIdentifierUseCase(
        schoolRepository: SchoolRepository,
        defaultLessonRepository: DefaultLessonRepository
    ): GetDefaultLessonByIdentifierUseCase {
        return GetDefaultLessonByIdentifierUseCase(
            schoolRepository = schoolRepository,
            defaultLessonRepository = defaultLessonRepository
        )
    }

    @Provides
    @Singleton
    fun provideVppIdLinkUseCases(
        vppIdRepository: VppIdRepository,
        profileRepository: ProfileRepository,
        keyValueRepository: KeyValueRepository,
        firebaseCloudMessagingManagerRepository: FirebaseCloudMessagingManagerRepository
    ): VppIdLinkUseCases {
        val testForMissingVppIdToProfileConnectionsUseCase = TestForMissingVppIdToProfileConnectionsUseCase(vppIdRepository, profileRepository)
        return VppIdLinkUseCases(
            getVppIdDetailsUseCase = GetVppIdDetailsUseCase(
                vppIdRepository = vppIdRepository,
                firebaseCloudMessagingManagerRepository = firebaseCloudMessagingManagerRepository,
                keyValueRepository = keyValueRepository,
                setBalloonUseCase = SetBalloonUseCase(keyValueRepository)
            ),
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
    fun provideAlarmManagerRepository(
        @ApplicationContext context: Context,
        db: VppDatabase
    ): AlarmManagerRepository {
        return AlarmManagerRepositoryImpl(
            context = context,
            alarmDao = db.alarmDao
        )
    }

    @Provides
    @Singleton
    fun provideTimetableRepository(
        db: VppDatabase
    ): TimetableRepository = TimetableRepositoryImpl(
        timetableDao = db.timetableDao,
        weekDao = db.weekDao,
        lessonTimeDao = db.lessonTimeDao,
        groupDao = db.groupDao
    )

    @Provides
    @Singleton
    fun provideWeekRepository(
        db: VppDatabase
    ): WeekRepository = WeekRepositoryImpl(db.weekDao)
}