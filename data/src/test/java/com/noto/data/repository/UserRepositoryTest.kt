package com.noto.data.repository

import com.noto.data.source.local.FakeUserDao
import com.noto.data.source.remote.FakeUserClient
import com.noto.domain.model.User
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

private val repositoryModule = module {

    single { FakeUserDao() }

    single { FakeUserClient() }

    single { UserRepositoryImpl(get<FakeUserDao>(), get<FakeUserClient>()) }

}

@ExperimentalCoroutinesApi
class UserRepositoryTest : KoinTest {

    private val userRepository by inject<UserRepositoryImpl>()

    private val user = User("John", "Doe", "JohnDao")

    @Before
    fun setUp() {
        startKoin {
            modules(repositoryModule)
        }
    }

    @Test
    fun createUser() = runBlocking {
        val result = userRepository.createUser(user)

        result.isSuccess shouldBe true
        result.getOrNull() shouldNotBe null
        result.getOrNull()?.userDisplayName shouldBe user.userDisplayName
        result.getOrNull()?.userEmail shouldBe user.userEmail
        result.getOrNull()?.userPassword shouldBe String()
        result shouldBeSuccess user.copy(userPassword = String())

    }

    @Test
    fun loginUser() = runBlocking {
        val result = userRepository.loginUser(user)

//        result.isSuccess shouldBe true
//        result.getOrNull() shouldNotBe null
//        result.getOrNull()?.userDisplayName shouldBe user.userDisplayName
//        result.getOrNull()?.userEmail shouldBe user.userEmail
//        result.getOrNull()?.userPassword shouldBe String()
//        result shouldBeSuccess user.copy(userPassword = String())
    }

}