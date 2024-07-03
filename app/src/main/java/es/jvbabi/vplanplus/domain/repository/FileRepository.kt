package es.jvbabi.vplanplus.domain.repository

import android.net.Uri

interface FileRepository {
    fun readBytes(uri: Uri): ByteArray?

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
}