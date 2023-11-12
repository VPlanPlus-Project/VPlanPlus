package es.jvbabi.vplanplus.ui.screens.onboarding.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor() : ViewModel() {

    private val _state = mutableStateOf(PermissionsState())
    val state: State<PermissionsState> = _state

    fun init(context: Context) {
        _state.value.permission.forEach {
            if (ContextCompat.checkSelfPermission(context, it.first.type) == PackageManager.PERMISSION_GRANTED) {
                _state.value = _state.value.copy(permission = _state.value.permission.map { pair ->
                    if (pair.first.type == it.first.type) Pair(pair.first, true)
                    else pair
                })
            }
        }
        _state.value = _state.value.copy(initDone = true)
    }

    fun nextPermission() {
        _state.value = _state.value.copy(index = _state.value.index + 1)
    }

    fun isLast(): Boolean {
        return _state.value.index == Permission.permissions.lastIndex
    }
}

data class PermissionsState(
    val index: Int = 0,
    val permission: List<Pair<Permission, Boolean>> = Permission.permissions.map { Pair(it, false) },
    val initDone: Boolean = false,
)