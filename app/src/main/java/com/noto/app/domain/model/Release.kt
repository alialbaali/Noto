package com.noto.app.domain.model

import com.noto.app.domain.model.Release.Changelog
import com.noto.app.domain.model.Release.Version
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

sealed interface Release {
    val version: Version
    val date: LocalDate
    val changelog: Changelog

    data class Version(val major: Int, val minor: Int, val patch: Int) {

        companion object {
            const val Current = "2.2.3"
            const val Last = "2.2.2"
        }

        override fun toString(): String = "$major.$minor.$patch"
    }

    @JvmInline
    value class Changelog(val text: String)
}

@Suppress("ClassName")
data class Release_1_8_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(1, 8, 0)
    override val date: LocalDate = LocalDate(2022, Month.JANUARY, 11)
}

@Suppress("ClassName")
data class Release_2_0_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 0, 0)
    override val date: LocalDate = LocalDate(2022, Month.FEBRUARY, 9)
}

@Suppress("ClassName")
data class Release_2_0_1(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 0, 1)
    override val date: LocalDate = LocalDate(2022, Month.FEBRUARY, 13)
}

@Suppress("ClassName")
data class Release_2_1_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 0)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 7)
}

@Suppress("ClassName")
data class Release_2_1_1(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 1)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 9)
}

@Suppress("ClassName")
data class Release_2_1_2(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 2)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 14)
}

@Suppress("ClassName")
data class Release_2_1_3(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 3)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 24)
}

@Suppress("ClassName")
data class Release_2_1_4(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 4)
    override val date: LocalDate = LocalDate(2022, Month.AUGUST, 2)
}

@Suppress("ClassName")
data class Release_2_1_5(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 5)
    override val date: LocalDate = LocalDate(2022, Month.AUGUST, 5)
}

@Suppress("ClassName")
data class Release_2_1_6(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 6)
    override val date: LocalDate = LocalDate(2022, Month.AUGUST, 7)
}

@Suppress("ClassName")
data class Release_2_2_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 0)
    override val date: LocalDate = LocalDate(2022, Month.NOVEMBER, 15)
}

@Suppress("ClassName")
data class Release_2_2_1(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 1)
    override val date: LocalDate = LocalDate(2023, Month.MARCH, 13)
}

@Suppress("ClassName")
data class Release_2_2_2(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 2)
    override val date: LocalDate = LocalDate(2023, Month.MARCH, 23)
}

@Suppress("ClassName")
data class Release_2_2_3(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 3)
    override val date: LocalDate = LocalDate(2023, Month.APRIL, 29)
}