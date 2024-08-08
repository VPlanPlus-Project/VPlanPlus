package es.jvbabi.vplanplus.util

fun sanitizeXml(xml: String): String = xml.trimStart { it != '<' }