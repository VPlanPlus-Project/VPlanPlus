package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.util.sanitizeXml
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

class RoomBaseData(rawXml: String) {
    val roomNames = mutableListOf<String>()

    init {
        val serializer = org.simpleframework.xml.core.Persister()
        val xml = sanitizeXml(rawXml)
        val reader = xml.reader()
        val rootObject: RaumSplan = serializer.read(RaumSplan::class.java, reader, false)
        roomNames.addAll(rootObject.list.map { it.short!! })

    }
}

@Root(name = "splan", strict = false)
private class RaumSplan {
    @field:ElementList(name = "Raeume", entry = "Ra") var list = mutableListOf<Raum>()
}

@Root(name = "Ra", strict = false)
private class Raum {
    @field:Element(name = "Kurz") var short: String? = null
}