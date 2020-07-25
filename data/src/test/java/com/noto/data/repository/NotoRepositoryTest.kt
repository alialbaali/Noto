package com.noto.data.repository

import com.noto.data.source.fake.FakeNotoDao
import com.noto.domain.model.Noto
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveElementAt
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContainNull
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.time.ZonedDateTime

private val notoRepositoryModule = module {

    single { FakeNotoDao() }

    single { NotoRepositoryImpl(get<FakeNotoDao>()) }

}

private const val LIBRARY_ID = 1L

class NotoRepositoryTest : KoinTest, StringSpec() {

    private val notoRepository by inject<NotoRepositoryImpl>()

    private val notos by lazy { runBlocking { notoRepository.getNotos(LIBRARY_ID).single().getOrThrow() } }

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

        "create noto"{

            runBlocking {

                notoRepository.createNoto(noto)

                notos shouldContain noto
                notos shouldHaveSize 1
                notos.shouldNotContainNull()
                notos.shouldHaveElementAt(0, noto)

            }

        }


        "update noto" {

            runBlocking {

                notoRepository.updateNoto(updatedNoto)

                val result = notos.find { it.notoId == noto.notoId }

                notoRepository.getNotos(LIBRARY_ID).single().getOrThrow() shouldContain updatedNoto
//                notos shouldContain updatedNoto
//                notos shouldNotContain noto

//                result shouldNotBe null
//                result!!.notoIsArchived shouldBe true
//                result.notoReminder shouldNotBe null

            }


        }


        "delete noto"{

        }

    }

}