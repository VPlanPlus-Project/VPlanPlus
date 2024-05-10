package es.jvbabi.vplanplus.feature.room_search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.GetRoomMapUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.RoomSearchUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomSearchModule {

    @Provides
    @Singleton
    fun provideRoomMapUseCase(
        roomRepository: RoomRepository,
        lessonRepository: LessonRepository,
        keyValueRepository: KeyValueRepository
    ): GetRoomMapUseCase {
        return GetRoomMapUseCase(
            roomRepository,
            lessonRepository,
            keyValueRepository
        )
    }

    @Provides
    @Singleton
    fun provideRoomSearchUseCases(
        getRoomMapUseCase: GetRoomMapUseCase,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ): RoomSearchUseCases {
        return RoomSearchUseCases(
            getCurrentIdentityUseCase,
            getRoomMapUseCase,
        )
    }
}