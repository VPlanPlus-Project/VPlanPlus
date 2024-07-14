package es.jvbabi.vplanplus.feature.room_search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.GroupRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCases
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.CanBookRoomUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.CancelBookingUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.GetClassLessonTimesUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.GetLessonTimesUseCase
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
    fun provideBookRoomUseCase(
        vppIdRepository: VppIdRepository,
        roomRepository: RoomRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ): BookRoomUseCase {
        return BookRoomUseCase(
            vppIdRepository = vppIdRepository,
            roomRepository = roomRepository,
            getCurrentProfileUseCase = getCurrentProfileUseCase
        )
    }

    @Provides
    @Singleton
    fun provideCancelBookingUseCase(
        vppIdRepository: VppIdRepository
    ): CancelBookingUseCase {
        return CancelBookingUseCase(vppIdRepository)
    }

    @Provides
    @Singleton
    fun provideCanBookRoomUseCase(
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ): CanBookRoomUseCase {
        return CanBookRoomUseCase(getCurrentProfileUseCase = getCurrentProfileUseCase)
    }

    @Provides
    @Singleton
    fun provideRoomSearchUseCases(
        groupRepository: GroupRepository,
        lessonTimeRepository: LessonTimeRepository,
        getRoomMapUseCase: GetRoomMapUseCase,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        canBookRoomUseCase: CanBookRoomUseCase,
        bookRoomUseCase: BookRoomUseCase,
        cancelBookingUseCase: CancelBookingUseCase
    ): RoomSearchUseCases {
        return RoomSearchUseCases(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            getRoomMapUseCase = getRoomMapUseCase,
            getLessonTimesUseCases = GetClassLessonTimesUseCase(
                lessonTimeRepository,
                groupRepository
            ),
            canBookRoomUseCase = canBookRoomUseCase,
            bookRoomUseCase = bookRoomUseCase,
            cancelBookingUseCase = cancelBookingUseCase
        )
    }

    @Provides
    @Singleton
    fun provideBookRoomUseCases(
        roomRepository: RoomRepository,
        keyValueRepository: KeyValueRepository,
        lessonTimeRepository: LessonTimeRepository,
        lessonRepository: LessonRepository,
        bookRoomUseCase: BookRoomUseCase,
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
    ): BookRoomUseCases {
        return BookRoomUseCases(
            getRoomByNameUseCase = GetRoomByNameUseCase(roomRepository),
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            getLessonTimesUseCase = GetLessonTimesUseCase(lessonTimeRepository, lessonRepository, roomRepository, keyValueRepository),
            hideRoomBookingDisclaimerBannerUseCase = HideRoomBookingDisclaimerBannerUseCase(keyValueRepository),
            showRoomBookingDisclaimerBannerUseCase = IsShowRoomBookingDisclaimerBannerUseCase(keyValueRepository),
            bookRoomUseCase = bookRoomUseCase
        )
    }
}