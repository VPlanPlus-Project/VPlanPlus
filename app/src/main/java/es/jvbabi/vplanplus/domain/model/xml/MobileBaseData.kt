package es.jvbabi.vplanplus.domain.model.xml

import es.jvbabi.vplanplus.domain.model.SchoolDownloadMode
import es.jvbabi.vplanplus.domain.repository.BaseData
import es.jvbabi.vplanplus.domain.repository.BaseDataClass
import es.jvbabi.vplanplus.util.sanitizeXml
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.Text
import org.simpleframework.xml.core.Persister
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MobileBaseData(
    rawXml: String,
    couldUseTimetable: Boolean
) {
    var baseData: BaseData

    init {
        val serializer: Serializer = Persister()
        val xml = sanitizeXml(rawXml)
        println(xml)
        val reader = xml.reader()
        val rootObject: VpPlan = serializer.read(VpPlan::class.java, reader, false)
        baseData = BaseData(
            classes = rootObject.classes.orEmpty().map { schoolClass ->
                BaseDataClass(
                    name = schoolClass.className,
                    lessonTimes = schoolClass.lessonTimes.orEmpty().mapNotNull { lessonTime ->
                        if (lessonTime.lessonNumber == -1 || lessonTime.from.isNullOrBlank() || lessonTime.to.isNullOrBlank()) return@mapNotNull null
                        lessonTime.lessonNumber to (lessonTime.from!! to lessonTime.to!!)
                    }.toMap()
                )
            },
            rooms = null,
            teachers = null,
            downloadMode = SchoolDownloadMode.INDIWARE_MOBIL,
            daysPerWeek = rootObject.head?.daysPerWeek ?: 5,
            holidays = rootObject.holidays.orEmpty().map { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyMMdd")) },
            canUseTimetable = couldUseTimetable
        )
        1+1
        rootObject.classes
    }
}

@Root(name = "VpMobil", strict = false)
private class VpPlan {
    @field:Element(name = "Kopf")
    var head: VpMobilKopf? = null

    /**
     * Formatted in YYMMDD
     */
    @field:ElementList(name = "FreieTage", entry = "ft")
    var holidays: List<String>? = null
    @field:ElementList(name = "Klassen", entry = "Kl")
    var classes: List<VpMobilKlasse>? = null
}

@Root(name = "Kopf", strict = false)
private class VpMobilKopf {
    @field:Element(name = "tageprowoche")
    var daysPerWeek: Int = 0
}

@Root(name = "Kl", strict = false)
private class VpMobilKlasse {
    @field:Element(name = "Kurz")
    var className: String = ""
    @field:ElementList(name = "KlStunden", required = false)
    var lessonTimes: List<VpMobilKlasseStunde>? = null
    @field:Element(name = "Unterricht")
    var defaultLessonsWrapper: VpMobilKlasseUnterrichtWrapper? = null
}

@Root(name = "KlSt", strict = false)
private class VpMobilKlasseStunde {

    /**
     * format: HH:mm
     */
    @field:Attribute(name = "ZeitVon")
    var from: String? = null

    /**
     * format: HH:mm
     */
    @field:Attribute(name = "ZeitBis")
    var to: String? = null

    @field:Text
    var lessonNumber: Int = -1
}

@Root(name = "Ue", strict = false)
private class VpMobilKlasseUnterrichtWrapper {
    @field:Element(name = "UeNr", required = false)
    var defaultLesson: VpMobilKlasseUnterrichtEntry? = null
}

@Root(name = "UeNr", strict = false)
private class VpMobilKlasseUnterrichtEntry {
    @field:Attribute(name = "UeFa", required = false)
    val subject: String? = null
    @field:Attribute(name = "UeLe", required = false)
    val teacher: String? = null
    @field:Attribute(name = "UeGr", required = false)
    val courseGroup: String? = null
    @field:Text
    val vpId: Int? = null
}