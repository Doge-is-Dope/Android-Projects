package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.ReminderDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest : KoinTest {


    val dataSource by inject<ReminderDataSource>()

    private lateinit var application: Application


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() = runBlocking {
//        val reminder1 = ReminderDTO(
//            "title1", "description1", "location1", 1.1, 1.1, "android"
//        )
//        val reminder2 = ReminderDTO(
//            "title2", "description2", "location2", 2.2, 2.2, "awesome"
//        )
//
//        dataSource.saveReminder(reminder1)
//        dataSource.saveReminder(reminder2)
        application = ApplicationProvider.getApplicationContext()
    }

    @After
    fun clearUp() = runBlocking {
        dataSource.deleteAllReminders()
    }

    @Test
    fun reminderList_displayNoData() {

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
        onView(withId(R.id.noDataTextView)).check(matches(withText(application.getString(R.string.no_data))))
    }


    @Test
    fun clickFab_navigateToSaveReminder() {

        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(ReminderListFragmentDirections.toSaveReminder())
    }
}