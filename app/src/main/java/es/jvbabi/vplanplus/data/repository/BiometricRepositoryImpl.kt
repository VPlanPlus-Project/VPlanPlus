package es.jvbabi.vplanplus.data.repository

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import es.jvbabi.vplanplus.domain.repository.BiometricRepository
import es.jvbabi.vplanplus.domain.repository.BiometricStatus

class BiometricRepositoryImpl(
    val context: Context
) : BiometricRepository {
    private val manager = BiometricManager.from(context)
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun canAuthenticate(): BiometricStatus {
        return when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NOT_SUPPORTED
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.NOT_READY
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_SET_UP
            else -> BiometricStatus.NOT_SUPPORTED
        }
    }

    override fun promptUser(
        title: String,
        subtitle: String,
        cancelString: String,
        activity: FragmentActivity,
        onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit,
        onFailed: () -> Unit,
        onError: (errorCode: Int, errorString: String) -> Unit
    ) {
        when(canAuthenticate()) {
            BiometricStatus.NOT_SUPPORTED -> {
                onError(BiometricStatus.NOT_SUPPORTED.ordinal, "Biometric authentication is not supported on this device")
                return
            }
            BiometricStatus.NOT_READY -> {
                onError(BiometricStatus.NOT_READY.ordinal, "Biometric authentication is not ready")
                return
            }
            BiometricStatus.NOT_SET_UP -> {
                onError(BiometricStatus.NOT_SET_UP.ordinal, "Biometric authentication is not set up")
                return
            }
            else -> Unit
        }

        biometricPrompt = BiometricPrompt(
            activity,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(cancelString)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
}