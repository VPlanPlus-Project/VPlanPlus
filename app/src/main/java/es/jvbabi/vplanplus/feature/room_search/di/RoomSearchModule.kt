package es.jvbabi.vplanplus.feature.room_search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.domain.usecase.profile.GetLessonTimesForClassUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCases
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.GetRoomByNameUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.GetRoomMapUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.HideRoomBookingDisclaimerBannerUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.IsShowRoomBookingDisclaimerBannerUseCase
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

    @Provides
    @Singleton
    fun provideBookRoomUseCases(
        roomRepository: RoomRepository,
        keyValueRepository: KeyValueRepository,
        vppIdRepository: VppIdRepository,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
        getLessonTimesForClassUseCase: GetLessonTimesForClassUseCase
    ): BookRoomUseCases {
        return BookRoomUseCases(
            getRoomByNameUseCase = GetRoomByNameUseCase(roomRepository),
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
            getLessonTimesForClassUseCase = getLessonTimesForClassUseCase,
            hideRoomBookingDisclaimerBannerUseCase = HideRoomBookingDisclaimerBannerUseCase(keyValueRepository),
            showRoomBookingDisclaimerBannerUseCase = IsShowRoomBookingDisclaimerBannerUseCase(keyValueRepository),
            bookRoomUseCase = BookRoomUseCase(
                vppIdRepository = vppIdRepository,
                roomRepository = roomRepository,
                getCurrentIdentityUseCase = getCurrentIdentityUseCase
            )
        )
    }
}