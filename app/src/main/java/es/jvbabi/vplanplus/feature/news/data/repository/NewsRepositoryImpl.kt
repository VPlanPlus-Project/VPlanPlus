package es.jvbabi.vplanplus.feature.news.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.google.gson.Gson
import es.jvbabi.vplanplus.R
import es.jvbabi.vplanplus.data.model.online.my.MessageResponse
import es.jvbabi.vplanplus.data.source.database.dao.MessageDao
import es.jvbabi.vplanplus.domain.model.Importance
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.repository.NotificationRepository
import es.jvbabi.vplanplus.shared.data.API_VERSION
import es.jvbabi.vplanplus.shared.data.NetworkRepositoryImpl
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NewsRepositoryImpl(
    private val networkRepository: NetworkRepositoryImpl,
    private val messageDao: MessageDao,
    private val context: Context,
    private val notificationRepository: NotificationRepository
) : MessageRepository {

    override fun getMessages(): Flow<List<Message>> {
        return messageDao.getMessages(getAppVersion(context)?.versionNumber?.toInt()?:0)
    }

    override fun getUnreadMessages(): Flow<List<Message>> {
        return messageDao.getUnreadMessages(getAppVersion(context)?.versionNumber?.toInt()?:0)
    }

    override fun getMessage(messageId: String): Flow<Message> {
        return messageDao.getMessage(messageId)
    }

    override suspend fun updateMessages(schoolId: Int?) {
        val version = getAppVersion(context)?.versionNumber?.toInt()?:0

        val url = if (schoolId == null) "/api/$API_VERSION/schools/news?version=$version"
        else "/api/$API_VERSION/school/$schoolId/news?version=$version"

        val response = networkRepository.doRequest(url)
        if (response.response != HttpStatusCode.OK || response.data == null) return
        val messages = Gson().fromJson(response.data, MessageResponse::class.java).items.map {
            it.toMessage()
        }
        messageDao.insertMessages(messages)
        val doNotSend = getMessages().first().filter { it.isRead || it.notificationSent }

        val criticalNew = messages
            .filter { it.importance == Importance.HIGH }
            .filter { !doNotSend.map { ds -> ds.id }.contains(it.id) }

        if (criticalNew.isNotEmpty()) notificationRepository.sendNotification(
            "NEWS",
            title = context.getString(R.string.notification_criticalNewsTitle),
            message = context.resources.getQuantityString(R.plurals.notification_criticalNewsText, criticalNew.size, criticalNew.size, criticalNew.joinToString { "\n - ${it.title}" }),
            id = 20,
            icon = R.drawable.vpp,
        )

        criticalNew.forEach {
            messageDao.markMessageAsSent(it.id)
        }
    }

    override suspend fun markMessageAsRead(messageId: String) {
        messageDao.markMessageAsRead(messageId)
    }
}


// https://medium.com/make-apps-simple/get-the-android-app-version-programmatically-5ba27d6a37fe
data class AppVersion(
    val versionName: String,
    val versionNumber: Long,
)

fun getAppVersion(
    context: Context,
): AppVersion? {
    return try {
        val packageManager = context.packageManager
        val packageName = context.packageName
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
        AppVersion(
            versionName = packageInfo.versionName,
            versionNumber = PackageInfoCompat.getLongVersionCode(packageInfo),
        )
    } catch (e: Exception) {
        null
    }
}
