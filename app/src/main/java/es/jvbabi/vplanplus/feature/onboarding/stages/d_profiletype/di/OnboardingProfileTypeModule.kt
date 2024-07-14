package es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.DefaultLessonRepository
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SchoolRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.IsFirstProfileForSchoolUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.OnboardingProfileTypeUseCases
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.PrepareNewProfileForSchoolUseCase
import es.jvbabi.vplanplus.feature.onboarding.stages.d_profiletype.domain.usecase.SetProfileTypeUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingProfileTypeModule {

    @Provides
    @Singleton
    fun provideOnboardingProfileTypeUseCases(
        keyValueRepository: KeyValueRepository,
        schoolRepository: SchoolRepository,
        groupRepository: GroupRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        vppIdRepository: VppIdRepository,
        defaultLessonRepository: DefaultLessonRepository
    ) = OnboardingProfileTypeUseCases(
        isFirstProfileForSchoolUseCase = IsFirstProfileForSchoolUseCase(keyValueRepository),
        setProfileTypeUseCase = SetProfileTypeUseCase(keyValueRepository),
        prepareNewProfileForSchoolUseCase = PrepareNewProfileForSchoolUseCase(
            keyValueRepository = keyValueRepository,
            schoolRepository = schoolRepository,
            groupRepository = groupRepository,
            teacherRepository = teacherRepository,
            roomRepository = roomRepository,
            vppIdRepository = vppIdRepository,
            defaultLessonRepository = defaultLessonRepository
        )
    )
}