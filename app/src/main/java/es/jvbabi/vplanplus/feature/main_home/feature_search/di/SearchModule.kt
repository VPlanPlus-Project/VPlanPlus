package es.jvbabi.vplanplus.feature.main_home.feature_search.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase
import es.jvbabi.vplanplus.feature.main_home.feature_search.domain.usecase.SearchUseCase
import es.jvbabi.vplanplus.feature.main_home.feature_search.domain.usecase.SearchUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchUseCases(
        @ApplicationContext context: Context,
        groupRepository: GroupRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        profileRepository: ProfileRepository,
        keyValueRepository: KeyValueRepository,
        planRepository: PlanRepository,
        timeRepository: TimeRepository
    ): SearchUseCases {
        return SearchUseCases(
            getCurrentProfileUseCase = GetCurrentProfileUseCase(
                keyValueRepository = keyValueRepository,
                profileRepository = profileRepository
            ),
            isSyncRunningUseCase = IsSyncRunningUseCase(context),
            searchUseCase = SearchUseCase(
                groupRepository = groupRepository,
                teacherRepository = teacherRepository,
                roomRepository = roomRepository,
                keyValueRepository = keyValueRepository,
                planRepository = planRepository
            ),
            getCurrentTimeUseCase = GetCurrentTimeUseCase(timeRepository)
        )
    }
}