package com.noto.domain

import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.test.KoinTest

open class BaseSpec(private vararg val modules: Module = emptyArray()) : StringSpec(), KoinTest {

    override fun beforeEach(testCase: TestCase) {
        super.beforeEach(testCase)
        if (modules.isNotEmpty())
            startKoin { modules(*modules) }
    }

    override fun afterEach(testCase: TestCase, result: TestResult) {
        super.afterEach(testCase, result)
        stopKoin()
    }

}