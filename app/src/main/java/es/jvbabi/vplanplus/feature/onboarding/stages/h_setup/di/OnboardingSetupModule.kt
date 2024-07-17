package es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.HolidayRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.IsFirstProfileForSchoolUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.domain.usecase.OnboardingSetupUseCases
import es.jvbabi.vplanplus.feature.onboarding.stages.h_setup.domain.usecase.SetupUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.UpdateFcmTokenUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingSetupModule {

    @Provides
    @Singleton
    fun provideOnboardingSetupUseCases(
        schoolRepository: SchoolRepository,
        groupRepository: GroupRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        defaultLessonRepository: DefaultLessonRepository,
        holidayRepository: HolidayRepository,
        keyValueRepository: KeyValueRepository,
        lessonTimeRepository: LessonTimeRepository,
        profileRepository: ProfileRepository,
        updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase
    ) = OnboardingSetupUseCases(
        setupUseCase = SetupUseCase(
            defaultLessonRepository = defaultLessonRepository,
            groupRepository = groupRepository,
            roomRepository = roomRepository,
            schoolRepository = schoolRepository,
            teacherRepository = teacherRepository,
            holidayRepository = holidayRepository,
            keyValueRepository = keyValueRepository,
            lessonTimeRepository = lessonTimeRepository,
            profileRepository = profileRepository,
            updateFcmTokenUseCase = UpdateFcmTokenUseCase(keyValueRepository, updateFirebaseTokenUseCase)
        ),
        isFirstProfileForSchoolUseCase = IsFirstProfileForSchoolUseCase(keyValueRepository)
    )
}