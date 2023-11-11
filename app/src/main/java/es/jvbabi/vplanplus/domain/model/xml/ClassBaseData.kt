package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.domain.model.Week
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister
import java.time.LocalDate

class ClassBaseData(val xml: String) {

    var schoolName: String
    var daysPerWeek: Int
    val classes = mutableListOf<String>()
    val schoolWeeks = mutableListOf<BaseDataSchoolWeek>()

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
        schoolWeeks.addAll(rootObject.schoolWeeks!!.map {
            val startString = it.start.split(".")
            val endString = it.end.split(".")

            val localDateFromNumbers = { // Kotlin does not detect correct method on LocalDate.of(year, month, day)
                    year: Int, month: Int, day: Int ->
                LocalDate.of(year, month, day)
            }

            val start = localDateFromNumbers(
                startString[2].toInt(),
                startString[1].toInt(),
                startString[0].toInt()
            )

            val end = localDateFromNumbers(
                endString[2].toInt(),
                endString[1].toInt(),
                endString[0].toInt()
            )

            BaseDataSchoolWeek(
                start = start,
                end = end,
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

    @field:Element(name = "Basisdaten")
    var baseData: BaseData? = null
}

private class SPlanHead {
    @field:Element(name = "schulname")
    var schoolName: String = ""
}

private class BaseData {
    @field:Element(name = "BaTageProWoche") var daysPerWeek: Int = 0
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
    val start: LocalDate,
    val end: LocalDate,
    val type: String,
    val week: Int
) {
    fun toWeek(schoolId: Long): Week {
        return Week(
            week = week,
            start = start,
            end = end,
            type = type,
            schoolId = schoolId
        )
    }
}