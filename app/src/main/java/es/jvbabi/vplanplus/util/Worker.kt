package es.jvbabi.vplanplus.util

import android.content.Context
import androidx.work.WorkManager
import androidx.work.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object Worker {

    fun isWorkerRunning(tag: String, context: Context): Flow<Boolean> = flow {
        while (true) {
            emit(WorkManager.getInstance(context).getWorkInfosByTag(tag).await().any { it.state == androidx.work.WorkInfo.State.RUNNING })
        }
    }
}