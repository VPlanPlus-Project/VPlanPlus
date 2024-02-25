package es.jvbabi.vplanplus.feature.settings.vpp_id.ui.domain.usecase

data class AccountSettingsUseCases(
    val getAccountsUseCase: GetAccountsUseCase,
    val testAccountUseCase: TestAccountUseCase,
    val deleteAccountUseCase: DeleteAccountUseCase,
    val getSessionsUseCase: GetSessionsUseCase,
    val closeSessionUseCase: CloseSessionUseCase
)
