package com.noto.app

import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.library.LibraryViewModel
import com.noto.app.library.NotoColorListAdapter
import com.noto.app.note.NoteViewModel
import com.noto.app.util.LayoutManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.parameter.parametersOf
import org.koin.test.KoinTest
import org.koin.test.get
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@LargeTest
class AppActivityTest : KoinTest {

    @get:Rule
    var activityScenarioRule = activityScenarioRule<AppActivity>()

    private lateinit var navController: NavHostController
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var libraryRepository: LibraryRepository
    private lateinit var noteRepository: NoteRepository
    private lateinit var localStorage: LocalStorage

    @Before
    fun setup() {
        libraryViewModel = get { parametersOf(0L) }
        noteViewModel = get { parametersOf(59L, 0L) }
        libraryRepository = get()
        noteRepository = get()
        localStorage = get()
        runBlocking {
            libraryRepository.clearLibraries()
            noteRepository.clearNotes()
            localStorage.clear()
        }
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        activityScenarioRule.scenario.onActivity { activity ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(activity.findViewById(android.R.id.content), navController)
        }
    }

    @Test
    fun no_libraries_displayed_first_time_the_application_starts() {
        onView(withId(R.id.tv_place_holder))
            .check(matches(withText(R.string.no_libraries)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun create_new_library_should_display_library() {
        libraryViewModel.createOrUpdateLibrary("Work")

        shortTimeDelay()

        onView(withId(R.id.tv_place_holder))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(2)))
            .check(matches(withChild(hasDescendant(withText("Work")))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun navigate_to_new_library_dialog_fragment() {
        onView(withId(R.id.fab))
            .perform(click())

        onView(withId(R.id.ll))
            .check(matches(isDisplayed()))

        onView(withId(R.id.et))
            .check(matches(isFocused()))
            .check(matches(withHint(R.string.library_title)))
    }

    @Test
    fun navigate_to_empty_library_by_clicking_library_item() {
        libraryViewModel.createOrUpdateLibrary("Work")

        shortTimeDelay()

        onView(withId(R.id.rv))
            .perform(actionOnItem<EpoxyViewHolder>(hasDescendant(withText("Work")), click()))

        onView(withId(R.id.tv_place_holder))
            .check(matches(withText(R.string.library_is_empty)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun navigate_to_main_dialog_fragment() {
        onView(withContentDescription(R.string.more))
            .perform(click())

        onView(withId(R.id.ll))
            .check(matches(withChild(withId(R.id.tv_change_theme))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun navigate_to_settings_fragment() {
        onView(withContentDescription(R.string.more))
            .perform(click())

        onView(withId(R.id.tv_settings))
            .perform(click())

        onView(withId(R.id.tb))
            .check(matches(hasDescendant(withText(R.string.settings))))
    }

    @Test
    fun navigate_to_theme_dialog_fragment() {
        onView(withContentDescription(R.string.more))
            .perform(click())

        onView(withId(R.id.tv_change_theme))
            .perform(click())

        onView(withId(R.id.rb_system_theme))
            .check(matches(isChecked()))
    }

    @Test
    fun set_theme_to_dark() {
        onView(withContentDescription(R.string.more))
            .perform(click())

        onView(withId(R.id.tv_change_theme))
            .perform(click())

        onView(withId(R.id.rb_dark_theme))
            .perform(click())

        onView(withContentDescription(R.string.more))
            .perform(click())

        onView(withId(R.id.tv_change_theme))
            .perform(click())

        onView(withId(R.id.rb_dark_theme))
            .check(matches(isChecked()))
    }

    @Test
    fun navigate_to_library_dialog_fragment() {
        libraryViewModel.createOrUpdateLibrary("Work")

        shortTimeDelay()

        onView(withId(R.id.rv))
            .perform(actionOnItem<EpoxyViewHolder>(hasDescendant(withText("Work")), longClick()))

        onView(withId(R.id.ll))
            .check(matches(withChild(withId(R.id.tv_edit_library))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun change_main_fragment_layout_manager() {
        onView(withContentDescription(R.string.view))
            .perform(click())

        assertTrue { libraryViewModel.layoutManager.value == LayoutManager.Linear }
    }

    @Test
    fun create_library_through_library_dialog_fragment() {
        onView(withId(R.id.fab))
            .perform(click())

        onView(withId(R.id.et))
            .perform(typeText("Work"))

        onView(withId(R.id.rv))
            .perform(actionOnItemAtPosition<NotoColorListAdapter.NotoColorItemViewHolder>(10, click()))

        onView(withId(R.id.btn_create))
            .perform(click())

        onView(withId(R.id.et))
            .check(doesNotExist())

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(2)))
            .check(matches(withChild(hasDescendant(withText("Work")))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun create_empty_library_through_library_dialog_fragment() {
        onView(withId(R.id.fab))
            .perform(click())

        onView(withId(R.id.et))
            .perform(typeText(""))

        onView(withId(R.id.btn_create))
            .perform(click())

        onView(withId(R.id.et))
            .check(matches(isDisplayed()))
    }

    @Test
    fun edit_library() {
        libraryViewModel.createOrUpdateLibrary("Work")

        onView(withId(R.id.rv))
            .perform(actionOnItem<EpoxyViewHolder>(hasDescendant(withText("Work")), longClick()))

        onView(withId(R.id.tv_edit_library))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.et))
            .perform(clearText())
            .perform(typeText("Code"))

        onView(withId(R.id.btn_create))
            .perform(click())

        onView(withId(R.id.rv))
            .check(matches(withChild(hasDescendant(withText("Code")))))
    }

    @Test
    fun delete_library() {
        libraryViewModel.createOrUpdateLibrary("Work")

        onView(withId(R.id.rv))
            .perform(actionOnItem<EpoxyViewHolder>(hasDescendant(withText("Work")), longClick()))

        onView(withId(R.id.tv_delete_library))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.btn_confirm))
            .perform(click())

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(2)))
    }

    @Test
    fun create_note() {
        createNote()

        navigateBack()

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(1)))
            .check(matches(hasDescendant(withText("Title"))))
            .check(matches(hasDescendant(withText("Body"))))
    }

    @Test
    fun delete_note() {
        createNote()

        hideKeyboard()

        onView(withContentDescription(R.string.more))
            .perform(click())

        onView(withId(R.id.tv_delete_note))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.btn_confirm))
            .perform(click())

        onView(withId(R.id.snackbar_text))
            .check(matches(withText(R.string.note_is_deleted)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(0)))
    }

    @Test
    fun duplicate_note() {
        createNote()

        navigateBack()

        onView(withId(R.id.rv))
            .perform(actionOnItemAtPosition<EpoxyViewHolder>(0, longClick()))

        onView(withId(R.id.tv_duplicate_note))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.snackbar_text))
            .check(matches(withText(R.string.note_is_duplicated)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(2)))
    }

    @Test
    fun star_note() {
        createNote()

        navigateBack()

        onView(withId(R.id.rv))
            .perform(actionOnItemAtPosition<EpoxyViewHolder>(0, longClick()))

        onView(withId(R.id.tv_star_note))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.snackbar_text))
            .check(matches(withText(R.string.note_is_starred)))
            .check(matches(isDisplayed()))

        onView(withId(R.id.rv))
            .check(matches(withChild(hasDescendant(withContentDescription(R.string.star_note)))))
    }

    @Test
    fun archive_note() {
        createNote()

        navigateBack()

        onView(withId(R.id.rv))
            .perform(actionOnItemAtPosition<EpoxyViewHolder>(0, longClick()))

        onView(withId(R.id.tv_archive_note))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.snackbar_text))
            .check(matches(withText(R.string.note_is_archived)))
            .check(matches(isDisplayed()))

        onView(withContentDescription(R.string.archived_notes))
            .perform(click())

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(1)))
    }

    private fun createNote() {
        libraryViewModel.createOrUpdateLibrary("Work")

        onView(withId(R.id.rv))
            .perform(actionOnItemAtPosition<EpoxyViewHolder>(1, click()))

        onView(withId(R.id.rv))
            .check(matches(hasChildCount(0)))

        onView(withId(R.id.fab))
            .perform(click())

        onView(withId(R.id.et_note_title))
            .perform(typeText("Title"))

        onView(withId(R.id.et_note_body))
            .perform(typeText("Body"))
    }

    private fun navigateBack() = onView(withContentDescription(R.string.back))
        .perform(click())

    private fun hideKeyboard() = onView(isRoot())
        .perform(closeSoftKeyboard())

    private fun shortTimeDelay(timeMillis: Long = 50) = runBlocking {
        delay(timeMillis)
    }

}