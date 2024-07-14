package es.jvbabi.vplanplus.domain.model.xml

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister

class ClassBaseData(val xml: String) {

    var schoolName: String
    var daysPerWeek: Int
    val classes = mutableListOf<String>()

    val holidays =
        mutableListOf<Pair<Triple<Int, Int, Int>, Boolean>>() // Pair<<year, month, day>, is public holiday> TODO convert to class

    init {
        val serializer: Serializer = Persister()
        val reader = xml.reader()
        val rootObject: Splan = serializer.read(Splan::class.java, reader, false)

        schoolName = rootObject.head!!.schoolName
        daysPerWeek = rootObject.baseData!!.daysPerWeek

        holidays.addAll(rootObject.holidays!!.map {
            Pair(
                Triple(
                    2000 + (it.date.substring(0, 2).toInt()),
                    it.date.substring(2, 4).toInt(),
                    it.date.substring(4, 6).toInt()
                ), it.isPublicHoliday == "1"
            )
        })
        classes.addAll(rootObject.classes!!.map { it.schoolClass })
    }
}

@Root(name = "splan", strict = false)
private class Splan {
    @field:Element(name = "Kopf")
    var head: SPlanHead? = null

    @field:ElementList(name = "FreieTage", entry = "ft")
    var holidays: List<Holiday>? = null

    @field:ElementList(name = "Klassen")
    var classes: List<SchoolClass>? = null

    @field:Element(name = "Basisdaten")
    var baseData: BaseData? = null
}

@Root(name = "Kopf", strict = false)
private data class SPlanHead @JvmOverloads constructor(
    @field:Element(name = "schulname")
    var schoolName: String = ""
)

@Root(name = "Basisdaten", strict = false)
private data class BaseData @JvmOverloads constructor(
    @field:Element(name = "BaTageProWoche") var daysPerWeek: Int = 0
)

private class SchoolClass {
    @field:Element(name = "Kurz")
    var schoolClass: String = ""
}

@Root(name = "FreieTage", strict = false)
private class Holiday {
    @field:Text(required = false)
    var date: String = ""
    @field:Attribute(name = "feier", required = false)
    var isPublicHoliday: String = ""
}