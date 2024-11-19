package es.jvbabi.vplanplus.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

object Worker {

    @SuppressLint("RestrictedApi") // todo
    fun isWorkerRunningFlow(tag: String, context: Context): Flow<Boolean> = flow {
        var oldState: Boolean? = null
        while (true) {
            val state = WorkManager.getInstance(context).getWorkInfosByTag(tag).get()
                .any { it.state == WorkInfo.State.RUNNING }
            if (state != oldState) {
                oldState = state
                emit(state)
            }
        }
    }
        .flowOn(Dispatchers.IO)
}