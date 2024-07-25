package es.jvbabi.vplanplus.domain.repository

import java.io.File

interface FileRepository {
    fun readBytes(path: String): ByteArray?
    fun readBytes(file: File): ByteArray?

    /**
     * Writes the given bytes to the file at the given path.
     * @param folder The path to the file, relative to /data/data/<package_name>/files
     * @param fileName The name of the file
     * @param bytes The bytes to write
     */
    fun writeBytes(folder: String, fileName: String, bytes: ByteArray)

    /**
     * Deletes the file at the given path.
     * @param folder The path to the file, relative to /data/data/<package_name>/files
     * @param fileName The name of the file
     */
    fun deleteFile(folder: String, fileName: String)

    fun exists(folder: String, fileName: String): Boolean

    companion object {
        fun createSafeFileName(fileName: String): String {
            return fileName.replace(Regex("[^a-zA-Z0-9\\-._]", RegexOption.IGNORE_CASE), "_")
        }
    }
}