package es.jvbabi.vplanplus.feature.onboarding.stages.g_permissions.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import es.jvbabi.vplanplus.ui.common.Permission

class OnboardingPermissionsViewModel : ViewModel() {
    var state by mutableStateOf(OnboardingPermissionsState())

    fun init(context: Context) {
        updatePermissions(context)
        state = if (state.permissions.any { !it.value }) state.copy(currentIndex = 0)
        else state.copy(allDone = true)
    }

    private fun updatePermissions(context: Context) {
        state = state.copy(permissions = Permission.onboardingPermissions.associateWith { it.isGranted(context) })
    }

    fun doAction(action: UiAction) {
        when (action) {
            is Next -> {
                val next = state.permissions.toList().drop((state.currentIndex ?: 0)+1).indexOfFirst { !it.second }
                updatePermissions(action.context)
                state = if (next == -1) state.copy(currentIndex = null, allDone = true)
                else state.copy(currentIndex = next + (state.currentIndex ?: 0))
            }
        }
    }
}

data class OnboardingPermissionsState(
    val permissions: Map<Permission, Boolean> = emptyMap(),
    val currentIndex: Int? = null,
    val allDone: Boolean = false
)

sealed class UiAction
data class Next(val context: Context) : UiAction()