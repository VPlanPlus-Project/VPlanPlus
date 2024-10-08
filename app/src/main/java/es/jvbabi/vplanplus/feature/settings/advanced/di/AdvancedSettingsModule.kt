package es.jvbabi.vplanplus.feature.settings.advanced.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.LessonRepository
import es.jvbabi.vplanplus.domain.repository.RoomRepository
import es.jvbabi.vplanplus.domain.repository.SystemRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetVppIdServerUseCase
import es.jvbabi.vplanplus.domain.usecase.sync.UpdateFirebaseTokenUseCase
import es.jvbabi.vplanplus.feature.main_grades.view.domain.repository.GradeRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.repository.HomeworkRepository
import es.jvbabi.vplanplus.feature.main_homework.shared.domain.usecase.HomeworkReminderUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.AdvancedSettingsUseCases
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.DeleteCacheUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.IsFcmDebugModeUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.ResetBalloonsUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.SetVppIdServerUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.ToggleFcmDebugModeUseCase
import es.jvbabi.vplanplus.feature.settings.advanced.domain.usecase.UpdateFcmTokenUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdvancedSettingsModule {

    @Provides
    @Singleton
    fun provideAdvancedSettingsUseCases(
        lessonRepository: LessonRepository,
        roomRepository: RoomRepository,
        gradeRepository: GradeRepository,
        homeworkRepository: HomeworkRepository,
        keyValueRepository: KeyValueRepository,
        systemRepository: SystemRepository,
        updateFirebaseTokenUseCase: UpdateFirebaseTokenUseCase
        ,
        homeworkReminderUseCase: HomeworkReminderUseCase
    ): AdvancedSettingsUseCases {
        return AdvancedSettingsUseCases(
            deleteCacheUseCase = DeleteCacheUseCase(
                lessonRepository,
                roomRepository,
                gradeRepository,
                homeworkRepository
            ),
            getVppIdServerUseCase = GetVppIdServerUseCase(keyValueRepository),
            setVppIdServerUseCase = SetVppIdServerUseCase(keyValueRepository, systemRepository),
            updateFcmTokenUseCase = UpdateFcmTokenUseCase(keyValueRepository, updateFirebaseTokenUseCase),
            toggleFcmDebugModeUseCase = ToggleFcmDebugModeUseCase(keyValueRepository),
            isFcmDebugModeUseCase = IsFcmDebugModeUseCase(keyValueRepository),
            resetBalloonsUseCase = ResetBalloonsUseCase(keyValueRepository),
            homeworkReminderUseCase = homeworkReminderUseCase
        )
    }
}