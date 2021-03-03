package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersLocalRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        remindersLocalRepository =
            RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrieveReminder() = runBlocking {

        val reminder = ReminderDTO("title", "description", "location", 1.0, 1.0, "id")
        remindersLocalRepository.saveReminder(reminder)

        val result = remindersLocalRepository.getReminder(reminder.id) as Result.Success

        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.location, `is`("location"))
        assertThat(result.data.latitude, `is`(1.0))
        assertThat(result.data.longitude, `is`(1.0))
        assertThat(result.data.id, `is`("id"))

    }

    @Test
    fun saveReminders_retrieveReminders() = runBlocking {

        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0, "id1")
        val reminder2 = ReminderDTO("title2", "description2", "location2", 2.0, 2.0, "id2")
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)


        val result = remindersLocalRepository.getReminders() as Result.Success


        assertThat(result.data.size, `is`(2))
        assertThat(result.data[1].title, `is`("title2"))
        assertThat(result.data[1].description, `is`("description2"))
        assertThat(result.data[1].location, `is`("location2"))
        assertThat(result.data[1].latitude, `is`(2.0))
        assertThat(result.data[1].longitude, `is`(2.0))
        assertThat(result.data[1].id, `is`("id2"))

    }

    @Test
    fun deleteReminders_returnEmptyList() = runBlocking {

        val reminder1 = ReminderDTO("title1", "description1", "location1", 1.0, 1.0, "id1")
        val reminder2 = ReminderDTO("title2", "description2", "location2", 2.0, 2.0, "id2")
        remindersLocalRepository.saveReminder(reminder1)
        remindersLocalRepository.saveReminder(reminder2)

        val addedResult = remindersLocalRepository.getReminders() as Result.Success

        assertThat(addedResult.data.size, `is`(2))

        remindersLocalRepository.deleteAllReminders()

        val deletedResult = remindersLocalRepository.getReminders() as Result.Success

        assertThat(deletedResult.data.isEmpty(), `is`(true))
    }

}