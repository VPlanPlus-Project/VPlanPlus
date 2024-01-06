package es.jvbabi.vplanplus.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import com.google.gson.Gson
import es.jvbabi.vplanplus.data.model.online.my.MessageResponse
import es.jvbabi.vplanplus.data.source.database.dao.MessageDao
import es.jvbabi.vplanplus.data.source.online.OnlineRequest
import es.jvbabi.vplanplus.domain.model.Message
import es.jvbabi.vplanplus.domain.repository.LogRecordRepository
import es.jvbabi.vplanplus.domain.repository.MessageRepository
import es.jvbabi.vplanplus.domain.usecase.Response
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageRepositoryImpl(
    private val messageDao: MessageDao,
    private val context: Context,
    logRecordRepository: LogRecordRepository
) : MessageRepository {

    private val onlineRequest = OnlineRequest(logRecordRepository)

    override fun getMessages(): Flow<List<Message>> {
        return messageDao.getMessages(getAppVersion(context)?.versionNumber?.toInt()?:0)
    }

    override fun getUnreadMessages(): Flow<List<Message>> {
        return messageDao.getUnreadMessages(getAppVersion(context)?.versionNumber?.toInt()?:0)
    }

    override fun getMessage(messageId: String): Flow<Message> {
        return messageDao.getMessage(messageId)
    }

    override suspend fun updateMessages(schoolId: Long?) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS'Z'")
        val formattedDateTime = LocalDateTime.now().format(formatter)
        val version = getAppVersion(context)?.versionNumber?.toInt()?:0
        val url = "https://database-00.jvbabi.es/api/collections/posts/records?expand=importance&perPage=100&filter=((school_id=${schoolId?:0}) && (not_before_date <= \"$formattedDateTime\") && (not_after_date >= \"$formattedDateTime\") && (not_before_version <= $version) && (not_after_version >= $version))"
            .replace("&&", "%26".repeat(2))
        val response = onlineRequest.getResponse(url = url)
        if (response.response != Response.SUCCESS || response.data == null) return
        val messages = Gson().fromJson(response.data, MessageResponse::class.java).items.map {
            Message(
                id = it.id,
                title = it.title,
                content = it.content,
                date = LocalDateTime.parse(it.created, formatter),
                isRead = false,
                importance = it.expand.importance.toImportance(),
                fromVersion = it.notBeforeVersion,
                toVersion = it.notAfterVersion,
                schoolId = it.schoolId.toLong()
            )
        }
        messageDao.insertMessages(messages)
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
