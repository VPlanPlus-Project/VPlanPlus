package es.jvbabi.vplanplus.feature.onboarding.domain.model.xml

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

class RoomBaseData(val xml: String) {
    val roomNames = mutableListOf<String>()

    init {
        val serializer = org.simpleframework.xml.core.Persister()
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