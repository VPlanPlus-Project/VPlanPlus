package es.jvbabi.vplanplus.ui.common.dialog.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
import es.jvbabi.vplanplus.feature.main_calendar.home.domain.usecase.GetDayUseCase
import es.jvbabi.vplanplus.ui.common.dialog.domain.usecase.SelectDateUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SelectDateModule {

    @Provides
    @Singleton
    fun provideSelectDateUseCases(
        getCurrentProfileUseCase: GetCurrentProfileUseCase,
        getDayUseCase: GetDayUseCase
    ): SelectDateUseCases {
        return SelectDateUseCases(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
            getDayUseCase = getDayUseCase
        )
    }
}