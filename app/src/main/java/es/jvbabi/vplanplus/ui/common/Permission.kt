package es.jvbabi.vplanplus.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import es.jvbabi.vplanplus.R

/**
 * Data class that represents a permission
 * @param type The type of the permission (android.Manifest.permission)
 * @param name The name of the permission (R.string)
 * @param description The description of the permission (R.string)
 */
data class Permission(
    val type: String,
    val name: Int,
    val description: Int
) {
    companion object {
        val onboardingPermissions = listOfNotNull(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                Permission(
                    type = Manifest.permission.POST_NOTIFICATIONS,
                    name = R.string.permission_notificationsTitle,
                    description = R.string.permission_notificationsText,
                )
            else null,
        )

        fun isGranted(context: Context, type: String): Boolean {
            return (ContextCompat.checkSelfPermission(context, type)
                    == PackageManager.PERMISSION_GRANTED)
        }
    }

    fun isGranted(context: Context): Boolean {
        return isGranted(context, type)
    }
}

@Composable
fun isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(LocalContext.current, permission) == PackageManager.PERMISSION_GRANTED
}

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}