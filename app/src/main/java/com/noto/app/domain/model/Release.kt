package com.noto.app.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

sealed interface Release {
    val version: String
    val date: LocalDate
    val changelog: String

    companion object {
        const val CurrentVersion = "1.8.0"
    }
}

data class Release_1_8_0(override val changelog: String) : Release {
    override val version: String = "1.8.0"
    override val date: LocalDate = LocalDate(2022, Month.JANUARY, 1)
}

class Release_1_9_0(override val changelog: String) : Release {
    override val version: String = "1.9.0"
    override val date: LocalDate = LocalDate(2022, Month.JANUARY, 1)
}

class Release_1_9_1(override val changelog: String) : Release {
    override val version: String = "1.9.1"
    override val date: LocalDate = LocalDate(2022, Month.JANUARY, 1)
}
