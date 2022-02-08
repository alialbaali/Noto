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
            const val Current = "1.8.0"
            const val Last = "1.7.2"
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