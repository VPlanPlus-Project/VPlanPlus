package es.jvbabi.vplanplus.ui.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import es.jvbabi.vplanplus.R

@Composable
fun unknownVppId(): String = stringResource(id = R.string.unknownVppId)

fun unknownVppId(context: Context) = context.getString(R.string.unknownVppId)