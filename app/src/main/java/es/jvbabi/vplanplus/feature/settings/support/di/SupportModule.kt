package es.jvbabi.vplanplus.feature.settings.support.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.di.VppModule.provideVppIdNetworkRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.ProfileRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentIdentityUseCase
import es.jvbabi.vplanplus.feature.logs.data.repository.LogRecordRepository
import es.jvbabi.vplanplus.feature.settings.support.data.repository.FeedbackRepositoryImpl
import es.jvbabi.vplanplus.feature.settings.support.domain.repository.FeedbackRepository
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.GetEmailForSupportUseCase
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.SendFeedbackUseCase
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.SupportUseCases
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.ValidateEmailUseCase
import es.jvbabi.vplanplus.feature.settings.support.domain.usecase.ValidateFeedbackUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupportModule {

    @Provides
    @Singleton
    fun provideFeedbackRepository(
        vppIdRepository: VppIdRepository,
        profileRepository: ProfileRepository,
        logRecordRepository: LogRecordRepository,
        keyValueRepository: KeyValueRepository
    ): FeedbackRepository {
        return FeedbackRepositoryImpl(
            vppIdRepository = vppIdRepository,
            profileRepository = profileRepository,
            vppIdNetworkRepository = provideVppIdNetworkRepository(keyValueRepository, logRecordRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSupportUseCases(
        feedbackRepository: FeedbackRepository,
        getCurrentIdentityUseCase: GetCurrentIdentityUseCase
    ) = SupportUseCases(
        getEmailForSupportUseCase = GetEmailForSupportUseCase(
            getCurrentIdentityUseCase = getCurrentIdentityUseCase,
        ),
        validateFeedbackUseCase = ValidateFeedbackUseCase(),
        validateEmailUseCase = ValidateEmailUseCase(),
        sendFeedbackUseCase = SendFeedbackUseCase(feedbackRepository)
    )
}