package es.jvbabi.vplanplus.util

import java.io.File

fun File.getFileSize(): Long = readBytes().size.toLong()