package com.noto.app.data.source

import androidx.test.runner.AndroidJUnit4
import com.noto.app.domain.source.LocalLibraryDataSource
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalLibraryDataSourceTest {

    private lateinit var source: LocalLibraryDataSource

//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()

//    init {
//        beforeTest {
//            startKoin {
//                val context = ApplicationProvider.getApplicationContext<Context>()
//                androidContext(context)
//                modules(inMemoryDatabaseModule)
//            }
//            source = get()
//        }
//
//        afterTest {
//            stopKoin()
//        }

//        "get all libraries should return empty list" {
//            source.getLibraries()
//                .single()
//                .shouldBeEmpty()
//        }
//    }
}