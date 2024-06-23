package es.jvbabi.vplanplus.feature.settings.support.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.jvbabi.vplanplus.di.VppModule.provideVppIdNetworkRepository
import es.jvbabi.vplanplus.domain.repository.KeyValueRepository
import es.jvbabi.vplanplus.domain.repository.VppIdRepository
import es.jvbabi.vplanplus.domain.usecase.general.GetCurrentProfileUseCase
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
        logRecordRepository: LogRecordRepository,
        keyValueRepository: KeyValueRepository
    ): FeedbackRepository {
        return FeedbackRepositoryImpl(
            vppIdRepository = vppIdRepository,
            vppIdNetworkRepository = provideVppIdNetworkRepository(keyValueRepository, logRecordRepository)
        )
    }

    @Provides
    @Singleton
    fun provideSupportUseCases(
        feedbackRepository: FeedbackRepository,
        getCurrentProfileUseCase: GetCurrentProfileUseCase
    ) = SupportUseCases(
        getEmailForSupportUseCase = GetEmailForSupportUseCase(
            getCurrentProfileUseCase = getCurrentProfileUseCase,
        ),
        validateFeedbackUseCase = ValidateFeedbackUseCase(),
        validateEmailUseCase = ValidateEmailUseCase(),
        sendFeedbackUseCase = SendFeedbackUseCase(feedbackRepository),
        getCurrentProfileUseCase = getCurrentProfileUseCase
    )
}