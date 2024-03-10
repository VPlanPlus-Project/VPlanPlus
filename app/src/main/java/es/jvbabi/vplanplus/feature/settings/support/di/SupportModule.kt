package es.jvbabi.vplanplus.feature.settings.support.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.GetEmailForSupportUseCase
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.SupportUseCases
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupportModule {

    @Provides
    @Singleton
    fun provideSupportUseCases(
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ) = SupportUseCases(
        getEmailForSupportUseCase = GetEmailForSupportUseCase(
            getCurrentIdentityUseCase = getCurrentIdentityUseCase
        )
    )
}