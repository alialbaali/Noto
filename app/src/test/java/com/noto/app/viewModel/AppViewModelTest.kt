package com.noto.app.viewModel

import com.noto.app.AppViewModel
import com.noto.app.di.appModule
import com.noto.app.domain.model.Theme
import com.noto.app.fakeLocalDataSourceModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get

class AppViewModelTest : StringSpec(), KoinTest {

    private lateinit var viewModel: AppViewModel

    init {
        beforeEach {
            startKoin {
                modules(appModule, fakeLocalDataSourceModule)
            }
            viewModel = get()
        }

        afterEach {
            stopKoin()
        }

        "get theme should return system theme by default" {
            viewModel.theme
                .first() shouldBe Theme.System
        }

        "update theme should set theme with the provided value" {
            viewModel.updateTheme(Theme.Light)
            viewModel.theme
                .first() shouldBe Theme.Light
        }
    }
}