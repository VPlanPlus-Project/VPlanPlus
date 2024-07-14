package es.jvbabi.vplanplus.data.repository

import android.content.Context
import androidx.core.net.toUri
import es.jvbabi.vplanplus.domain.repository.FileRepository
import java.io.File

class FileRepositoryImpl(
    private val context: Context
) : FileRepository {
    override fun readBytes(path: String): ByteArray? {
        val uri = context.filesDir.resolve(path).toUri()
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        inputStream.close()
        return bytes
    }

    override fun readBytes(file: File): ByteArray {
        val inputStream = file.inputStream()
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

    override fun exists(folder: String, fileName: String): Boolean {
        return File(context.filesDir, "$folder/$fileName").exists()
    }
}