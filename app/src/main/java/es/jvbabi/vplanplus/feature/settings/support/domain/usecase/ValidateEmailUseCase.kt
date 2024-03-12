package es.jvbabi.vplanplus.feature.settings.support.domain.usecase

class ValidateEmailUseCase {
    operator fun invoke(email: String): Boolean {
        return when {
            email.isBlank() -> true
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> true
            else -> false
        }
    }
}