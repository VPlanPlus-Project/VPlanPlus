package es.jvbabi.vplanplus.feature.onboarding.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.BaseDataRepository
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.StringRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VPlanRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.CheckSchoolIdSyntax
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.DefaultLessonUseCase
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.GetSchoolByIdUseCase
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.LoginUseCase
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.OnboardingUseCases
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.ProfileOptionsUseCase
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.SaveProfileUseCase
import es.jvbabi.vplanplus.feature.onboarding.domain.usecase.TestSchoolExistence
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingModule {

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
        stringRepository: StringRepository,
        notificationRepository: NotificationRepository,
        vppIdRepository: VppIdRepository
    ): OnboardingUseCases {
        return OnboardingUseCases(
            checkSchoolIdSyntax = CheckSchoolIdSyntax(schoolRepository),
            testSchoolExistence = TestSchoolExistence(schoolRepository),
            loginUseCase = LoginUseCase(schoolRepository, keyValueRepository, baseDataRepository),
            profileOptionsUseCase = ProfileOptionsUseCase(
                schoolRepository = schoolRepository,
                classRepository = classRepository,
                teacherRepository = teacherRepository,
                roomRepository = roomRepository,
                vppIdRepository = vppIdRepository,
                kv = keyValueRepository
            ),
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
                notificationRepository = notificationRepository,
                stringRepository = stringRepository
            ),
            getSchoolByIdUseCase = GetSchoolByIdUseCase(schoolRepository)
        )
    }
}