package es.jvbabi.vplanplus.domain.model.xml

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

class BaseDataParserStudents(val xml: String) {

    var schoolName: String
    val classes = mutableListOf<String>()
    val holidays = mutableListOf<String>()
    init {
        val serializer: Serializer = Persister()
        val reader = xml.reader()
        val rootObject: Splan = serializer.read(Splan::class.java, reader, false)
        schoolName = rootObject.head!!.schoolName
        holidays.addAll(rootObject.holidays!!)
        classes.addAll(rootObject.classes!!.map { it.schoolClass })
    }
}

@Root(name = "splan", strict = false)
private class Splan {
    @field:Element(name = "Kopf")
    var head: SPlanHead? = null

    @field:ElementList(name = "FreieTage")
    var holidays: List<String>? = null

    @field:ElementList(name = "Klassen")
    var classes: List<SchoolClass>? = null
}

private class SPlanHead {
    @field:Element(name = "schulname") var schoolName: String = ""
}

private class SchoolClass {
    @field:Element(name = "Kurz") var schoolClass: String = ""
}