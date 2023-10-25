package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.util.DateUtils
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister

class BaseDataParserStudents(val xml: String) {

    var schoolName: String
    val classes = mutableListOf<String>()
    val schoolWeeks = mutableListOf<BaseDataSchoolWeek>()

    val holidays =
        mutableListOf<Pair<Triple<Int, Int, Int>, Boolean>>() // Pair<<year, month, day>, is public holiday> TODO convert to class

    init {
        val serializer: Serializer = Persister()
        val reader = xml.reader()
        val rootObject: Splan = serializer.read(Splan::class.java, reader, false)
        schoolName = rootObject.head!!.schoolName
        holidays.addAll(rootObject.holidays!!.map {
            Pair(
                Triple(
                    2000 + (it.date.substring(0, 2).toInt()),
                    it.date.substring(2, 4).toInt(),
                    it.date.substring(4, 6).toInt()
                ), it.isPublicHoliday == "1"
            )
        })
        schoolWeeks.addAll(rootObject.schoolWeeks!!.map {
            val startString = it.start.split(".")
            val endString = it.end.split(".")

            val startTimestamp = DateUtils.getDayTimestamp(
                year = startString[2].toInt(),
                month = startString[1].toInt(),
                day = startString[0].toInt()
            )

            val endTimestamp = DateUtils.getDayTimestamp(
                year = endString[2].toInt(),
                month = endString[1].toInt(),
                day = endString[0].toInt()
            )

            BaseDataSchoolWeek(
                start = startTimestamp,
                end = endTimestamp,
                type = it.type,
                week = it.week.toInt()
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

    @field:ElementList(name = "Schulwochen", entry = "Sw")
    var schoolWeeks: List<SchoolWeek>? = null

    @field:ElementList(name = "Klassen")
    var classes: List<SchoolClass>? = null
}

private class SPlanHead {
    @field:Element(name = "schulname")
    var schoolName: String = ""
}

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

@Root(name = "Schulwochen", strict = false)
private class SchoolWeek {
    @field:Attribute(name = "SwDatumVon", required = true) var start: String = ""
    @field:Attribute(name = "SwDatumBis", required = true) var end: String = ""
    @field:Attribute(name = "SwWo", required = true) var type: String = ""
    @field:Text(required = true) var week: String = ""
}

data class BaseDataSchoolWeek(
    val start: Long,
    val end: Long,
    val type: String,
    val week: Int
)