package es.jvbabi.vplanplus.ui.common

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R
import kotlin.math.log10
import kotlin.math.pow

@Composable
fun unknownVppId(): String = stringResource(id = R.string.unknownVppId)

fun unknownVppId(context: Context) = context.getString(R.string.unknownVppId)

@SuppressLint("DefaultLocale")
fun storageToHumanReadableFormat(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
    return String.format("%.1f %s", bytes / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}