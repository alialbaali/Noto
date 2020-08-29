package com.noto.local

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest : StringSpec({
    "Length should be 0" {
        "".length shouldBe 0
    }
})
//    @Test
//    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
//    }
