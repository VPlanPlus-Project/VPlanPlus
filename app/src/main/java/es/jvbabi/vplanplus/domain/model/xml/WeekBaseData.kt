package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.util.sanitizeXml
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister

class WeekBaseData(val xml: String) {
    val times: HashMap<String, Map<Int, Pair<String, String>>> = HashMap()

    init {
        val serializer: Serializer = Persister()
        val reader = sanitizeXml(xml).reader()
        val rootObject: WeekSplan = serializer.read(WeekSplan::class.java, reader, false)

        rootObject.classes!!.forEach { classShort ->
            val classTimes = mutableMapOf<Int, Pair<String, String>>()
            classShort.hours!!.filter { it.start != "" && it.end != "" }.forEach forEachHour@{ hour ->
                val lessonNumber = hour.lessonNumber ?: return@forEachHour
                val start = hour.start ?: return@forEachHour
                val end = hour.end ?: return@forEachHour
                classTimes[lessonNumber] = Pair(start, end)
            }
            times[classShort.short!!] = classTimes
        }
    }
}

@Root(name = "splan", strict = false)
private class WeekSplan {
    @field:ElementList(name = "Klassen", entry = "Kl")
    var classes: List<WeekBaseClass>? = null
}

@Root(name = "Kl", strict = false)
private class WeekBaseClass {
    @field:Element(name = "Kurz")
    var short: String? = null
    @field:ElementList(name = "Stunden", entry = "St")
    var hours: List<WeekBaseHour>? = null
}

@Root(name = "St", strict = false)
private class WeekBaseHour {
    @field:Attribute(name = "StZeit", empty = "", required = false)
    var start: String? = null
    @field:Attribute(name = "StZeitBis", empty = "", required = false)
    var end: String? = null
    @field:Text(required = false)
    var lessonNumber: Int? = null
}