package com.noto.app.domain.model

import com.noto.app.domain.model.Release.Changelog
import com.noto.app.domain.model.Release.Version
import com.noto.app.util.Constants
import com.noto.app.util.format
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable

@Serializable
sealed interface Release {
    val version: Version
    val date: LocalDate
    val changelog: Changelog

    val githubUrl: String get() = Constants.Noto.GitHubReleaseUrl(versionFormatted)
    val isCurrent: Boolean get() = this.version == Version.Current
    val versionFormatted: String get() = version.format()
    val dateFormatted: String get() = date.format()

    @Serializable
    data class Version(val major: Int, val minor: Int, val patch: Int, val status: Status = Status.Stable) {

        companion object {
            val Current = Version(2, 3, 0, Status.RC)
            val Last = Version(2, 2, 3)
        }

        fun format(): String = if (status is Status.Beta) "$major.$minor.$patch-${status.format()}" else "$major.$minor.$patch"

        @Serializable
        sealed interface Status {
            @Serializable
            data class Beta(val version: Int) : Status {
                fun format() = "Beta$version"
            }

            @Serializable
            object RC : Status

            @Serializable
            object Stable : Status
        }
    }

    @JvmInline
    @Serializable
    value class Changelog(val changesIds: List<Int>)

    companion object
}

@Suppress("ClassName")
@Serializable
data class Release_1_8_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(1, 8, 0)
    override val date: LocalDate = LocalDate(2022, Month.JANUARY, 11)
}

@Suppress("ClassName")
@Serializable
data class Release_2_0_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 0, 0)
    override val date: LocalDate = LocalDate(2022, Month.FEBRUARY, 9)
}

@Suppress("ClassName")
@Serializable
data class Release_2_0_1(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 0, 1)
    override val date: LocalDate = LocalDate(2022, Month.FEBRUARY, 13)
}

@Suppress("ClassName")
@Serializable
data class Release_2_1_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 0)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 7)
}

@Suppress("ClassName")
@Serializable
data class Release_2_1_1(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 1)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 9)
}

@Suppress("ClassName")
@Serializable
data class Release_2_1_2(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 2)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 14)
}

@Suppress("ClassName")
@Serializable
data class Release_2_1_3(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 3)
    override val date: LocalDate = LocalDate(2022, Month.JULY, 24)
}

@Suppress("ClassName")
@Serializable
data class Release_2_1_4(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 4)
    override val date: LocalDate = LocalDate(2022, Month.AUGUST, 2)
}

@Suppress("ClassName")
@Serializable
data class Release_2_1_5(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 5)
    override val date: LocalDate = LocalDate(2022, Month.AUGUST, 5)
}

@Suppress("ClassName")
@Serializable
data class Release_2_1_6(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 1, 6)
    override val date: LocalDate = LocalDate(2022, Month.AUGUST, 7)
}

@Suppress("ClassName")
@Serializable
data class Release_2_2_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 0)
    override val date: LocalDate = LocalDate(2022, Month.NOVEMBER, 15)
}

@Suppress("ClassName")
@Serializable
data class Release_2_2_1(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 1)
    override val date: LocalDate = LocalDate(2023, Month.MARCH, 13)
}

@Suppress("ClassName")
@Serializable
data class Release_2_2_2(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 2)
    override val date: LocalDate = LocalDate(2023, Month.MARCH, 23)
}

@Suppress("ClassName")
@Serializable
data class Release_2_2_3(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 2, 3)
    override val date: LocalDate = LocalDate(2023, Month.APRIL, 29)
}

@Suppress("ClassName")
@Serializable
data class Release_2_3_0(override val changelog: Changelog) : Release {
    override val version: Version = Version(2, 3, 0, Version.Status.RC)
    override val date: LocalDate = LocalDate(2023, Month.AUGUST, 6)
}