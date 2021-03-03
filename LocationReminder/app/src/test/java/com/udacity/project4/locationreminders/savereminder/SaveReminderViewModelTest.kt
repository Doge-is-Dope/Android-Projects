package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.SingleLiveEvent

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.Is.`is`
import org.junit.*
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O])
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var fakeDataSource: FakeDataSource


    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() = mainCoroutineRule.runBlockingTest{
        fakeDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun cleanupDataSource() = runBlockingTest {
        fakeDataSource.deleteAllReminders()
        stopKoin()
    }

    @Test
    fun check_loading() {

        val reminder = ReminderDataItem("title", "description", "Trinity Church", 40.709054356523396, -74.01153804364777)

        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.validateAndSaveReminder(reminder)
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(saveReminderViewModel.showLoading.getOrAwaitValue(),`is`(false))
        assertThat(saveReminderViewModel.showToast.getOrAwaitValue(),`is`("Reminder Saved !"))
    }

    @Test
    fun shouldReturnError() {
        val reminderWithNoTitle = ReminderDataItem(null, "description", "Trinity Church", 40.709054356523396, -74.01153804364777)

        saveReminderViewModel.validateEnteredData(reminderWithNoTitle)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))

        val reminderWithNoSelectedLocation= ReminderDataItem("title", "", null, 40.709054356523396, -74.01153804364777)

        saveReminderViewModel.validateEnteredData(reminderWithNoSelectedLocation)
        assertThat(saveReminderViewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
    }
}