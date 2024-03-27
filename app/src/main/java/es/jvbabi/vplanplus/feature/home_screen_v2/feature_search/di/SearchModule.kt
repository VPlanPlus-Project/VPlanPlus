package es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.PlanRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.TeacherRepository
import es.jvbabi.vplanplus.domain.repository.TimeRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentTimeUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.IsSyncRunningUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.domain.usecase.SearchUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.feature_search.domain.usecase.SearchUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchModule {

    @Provides
    @Singleton
    fun provideSearchUseCases(
        @ApplicationContext context: Context,
        classRepository: ClassRepository,
        teacherRepository: TeacherRepository,
        roomRepository: RoomRepository,
        vppIdRepository: VppIdRepository,
        profileRepository: ProfileRepository,
        keyValueRepository: KeyValueRepository,
        planRepository: PlanRepository,
        timeRepository: TimeRepository
    ): SearchUseCases {
        return SearchUseCases(
            getCurrentIdentityUseCase = GetCurrentIdentityUseCase(
                vppIdRepository = vppIdRepository,
                classRepository = classRepository,
                keyValueRepository = keyValueRepository,
                profileRepository = profileRepository
            ),
            isSyncRunningUseCase = IsSyncRunningUseCase(context),
            searchUseCase = SearchUseCase(
                classRepository = classRepository,
                teacherRepository = teacherRepository,
                roomRepository = roomRepository,
                keyValueRepository = keyValueRepository,
                planRepository = planRepository
            ),
            getCurrentTimeUseCase = GetCurrentTimeUseCase(timeRepository)
        )
    }
}