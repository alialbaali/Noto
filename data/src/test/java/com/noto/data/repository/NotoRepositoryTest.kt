package com.noto.data.repository

import com.noto.data.source.fake.FakeNotoDao
import com.noto.domain.model.Noto
import com.noto.domain.repository.NotoRepository
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.ZonedDateTime

private val notoRepositoryModule = module {

    single { FakeNotoDao() }

    single<NotoRepository> { NotoRepositoryImpl(get<FakeNotoDao>()) }

}

private const val LIBRARY_ID = 1L

class NotoRepositoryTest : KoinTest, StringSpec() {

    private val notoRepository by inject<NotoRepository>()

    private val notos by lazy { runBlocking { notoRepository.getNotos().single() } }

    private val noto = Noto(libraryId = LIBRARY_ID, notoTitle = "TITLE", notoBody = "BODY", notoPosition = 0)

    private val updatedNoto = noto.copy(notoTitle = "UPDATED TITLE", notoBody = "UPDATED BODY", notoIsArchived = true, notoReminder = ZonedDateTime.now())

    override fun beforeSpec(spec: Spec) {
        super.beforeSpec(spec)
        startKoin { modules(notoRepositoryModule) }
    }

    override fun afterSpec(spec: Spec) {
        super.afterSpec(spec)
        getKoin().close()
    }

    init {

        "get notos"{

            val result = notoRepository.getNotos().single()

            result shouldHaveSize 0
            result.shouldBeEmpty()

        }

        "create noto"{

            notoRepository.createNoto(noto)

            notos shouldContain noto
            notos shouldHaveSize 1
            notos.shouldNotContainNull()
            notos.shouldHaveElementAt(0, noto)

        }


        "update noto" {

            notoRepository.updateNoto(updatedNoto)

            val result = notos.find { it.notoId == noto.notoId }

            notos shouldContain updatedNoto
            notos shouldNotContain noto

            result shouldNotBe null
            result!!.notoIsArchived.shouldBeTrue()
            result.notoReminder shouldNotBe null
            result.notoTitle shouldBe updatedNoto.notoTitle
            result.notoBody shouldBe updatedNoto.notoBody

        }

        "get noto"{

            val result = notoRepository.getNoto(noto.notoId).single()

            result shouldBe updatedNoto
            result shouldNotBe noto
            result.notoIsArchived.shouldBeTrue()
            result.notoReminder shouldNotBe null
            result.notoTitle shouldBe updatedNoto.notoTitle
            result.notoBody shouldBe updatedNoto.notoBody

        }

        "delete noto"{

            notoRepository.deleteNoto(updatedNoto)

            notos.shouldBeEmpty()
            notos shouldHaveSize 0
            notos shouldNotContain noto
            notos shouldNotContain updatedNoto

        }

    }

}