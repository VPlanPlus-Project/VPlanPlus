package es.jvbabi.vplanplus.feature.home_screen_v2.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.home_screen_v2.domain.usecase.HomeUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ): HomeUseCases {
        return HomeUseCases(
            getCurrentIdentityUseCase = getCurrentIdentityUseCase
        )
    }
}