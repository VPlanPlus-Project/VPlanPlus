package es.jvbabi.vplanplus.ui.preview

import es.jvbabi.vplanplus.domain.model.School

object School {

    fun generateRandomSchools(count: Int = 4): MutableList<School> {
        val schools = mutableListOf<School>()
        for (i in 0..count) {
            schools.add(
                School(
                    schoolId = i.toLong(),
                    name = randomSchoolName(),
                    "schueler",
                    "",
                    5,
                    true
                )
            )
        }
        return schools
    }

    private fun randomSchoolName(): String {
        return listOf(
            "Goethe Gymnasium",
            "Heinrich-Heine Realschule",
            "Ludwig-van-Beethoven Grundschule",
            "Sophie-Scholl Oberschule",
            "Wilhelm-Röntgen Gesamtschule",
            "Robert-Koch Mittelschule",
            "Marie-Curie Gymnasium",
            "Hermann-Hesse Schule",
            "Karl-Valentin Grundschule",
            "Johann-Wolfgang-von-Goethe Realschule",
            "Gustav-Mahler Musikschule",
            "Thomas-Mann Internat",
            "Clara-Schumann Akademie",
            "Albert-Schweitzer Schule",
            "Friedrich-Ebert Gemeinschaftsschule"
        ).random()
    }
}