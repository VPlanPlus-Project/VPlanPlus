package es.jvbabi.vplanplus.data.repository

import android.content.Context
import android.net.Uri
import es.jvbabi.vplanplus.domain.repository.FileRepository
import java.io.File

class FileRepositoryImpl(
    private val context: Context
) : FileRepository {
    override fun readBytes(uri: Uri): ByteArray? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        inputStream.close()
        return bytes
    }

    override fun writeBytes(folder: String, fileName: String, bytes: ByteArray) {
        val directory = File(context.filesDir, folder)
        if (!directory.exists()) directory.mkdirs()
        val output = File(directory, fileName)
        val outputStream = output.outputStream()
        outputStream.write(bytes)
        outputStream.close()
    }

    override fun deleteFile(folder: String, fileName: String) {
        val file = File(context.filesDir, "$folder/$fileName")
        file.delete()
    }
}