package es.jvbabi.vplanplus.feature.room_search.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.ClassRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.LessonTimeRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCase
import es.jvbabi.vplanplus.feature.room_search.domain.usecase.BookRoomUseCases
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
    fun provideRoomSearchUseCases(
        classRepository: ClassRepository,
        lessonTimeRepository: LessonTimeRepository,
        getRoomMapUseCase: GetRoomMapUseCase,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ): RoomSearchUseCases {
        return RoomSearchUseCases(
            getCurrentIdentityUseCase,
            getRoomMapUseCase,
            getLessonTimesUseCases = GetClassLessonTimesUseCase(
                lessonTimeRepository,
                classRepository
            )
        )
    }

    @Provides
    @Singleton
    fun provideBookRoomUseCases(
        roomRepository: RoomRepository,
        keyValueRepository: KeyValueRepository,
        lessonTimeRepository: LessonTimeRepository,
        lessonRepository: LessonRepository,
        vppIdRepository: VppIdRepository,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase,
    ): BookRoomUseCases {
        return BookRoomUseCases(
            getRoomByNameUseCase = GetRoomByNameUseCase(roomRepository),
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
            getLessonTimesUseCase = GetLessonTimesUseCase(lessonTimeRepository, lessonRepository, roomRepository, keyValueRepository),
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