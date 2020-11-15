package com.noto.data.repository

import com.noto.data.LabelRepositoryImpl
import com.noto.data.source.fake.FakeLabelDao
import com.noto.domain.model.Label
import com.noto.domain.model.NoteColor
import com.noto.domain.repository.LabelRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

private val labelRepositoryModule = module {

    single { FakeLabelDao() }

    single<LabelRepository> { LabelRepositoryImpl(get<FakeLabelDao>()) }

}

class LabelRepositoryTest : KoinTest, StringSpec() {

    private val labelRepository by inject<LabelRepository>()

    private val labels by lazy { labelRepository.getLabels().run { runBlocking { single() } } }

    private val label = Label(labelTitle = "Hello World", labelColor = NotoColor.TEAL)

    private val updatedLabel = label.copy(labelTitle = "Updated Label", labelColor = NotoColor.BLUE)

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        startKoin { modules(labelRepositoryModule) }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        getKoin().close()
    }

    init {

        "get labels"{

            labels shouldHaveSize 0
            labels.shouldBeEmpty()

        }

        "create label" {

            labelRepository.createLabel(label)

            labels shouldContain label
            labels shouldHaveSize 1

        }

        "update label" {

            labelRepository.updateLabel(updatedLabel)

            val result = labels.find { it.labelId == updatedLabel.labelId }

            labels shouldContain updatedLabel
            labels shouldNotContain label
            labels.first() shouldBe updatedLabel
            result shouldNotBe null
            result!!.labelTitle shouldBe updatedLabel.labelTitle
            result.labelColor shouldBe updatedLabel.labelColor

        }

        "get label"{

            val result = labelRepository.getLabel(label.labelId).single()

            result shouldBe updatedLabel
            result shouldNotBe label
            result.labelTitle shouldBe  updatedLabel.labelTitle
            result.labelColor shouldBe updatedLabel.labelColor

        }

        "remove label"{

            labelRepository.deleteLabel(updatedLabel)

            labels shouldNotContain label
            labels shouldNotContain updatedLabel
            labels shouldHaveSize 0

        }
    }

}