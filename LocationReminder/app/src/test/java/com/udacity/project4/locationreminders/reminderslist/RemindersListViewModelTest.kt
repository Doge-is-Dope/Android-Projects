package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config
import java.util.*

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O])
class RemindersListViewModelTest {

    private lateinit var reminderListViewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() = mainCoroutineRule.runBlockingTest {

        val reminder1 = ReminderDTO(
            "title1",
            "description1",
            "location1",
            1.111,
            1.111,
            "android"
        )
        val reminder2 = ReminderDTO(
            "title2",
            "description2",
            "location2",
            2.222,
            2.222,
            "awesome"
        )

        fakeDataSource = FakeDataSource()
        fakeDataSource.saveReminders(reminder1, reminder2)

        reminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun cleanupDataSource() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        stopKoin()
    }



    @Test
    fun check_loading() {
        mainCoroutineRule.pauseDispatcher()
        reminderListViewModel.loadReminders()
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(reminderListViewModel.showLoading.getOrAwaitValue(), `is`(false))

        assertThat(reminderListViewModel.remindersList.getOrAwaitValue()?.size, `is`(2))
        assertThat(reminderListViewModel.remindersList.getOrAwaitValue()?.first()?.title, `is`("title1"))

    }

    @Test
    fun shouldReturnError() {
        fakeDataSource.setReturnError(true)
        reminderListViewModel.loadReminders()

        assertThat(reminderListViewModel.showSnackBar.getOrAwaitValue(), `is`("Error when retrieving reminders"))
        assertThat(reminderListViewModel.showNoData.getOrAwaitValue() , `is`(true))
    }

}