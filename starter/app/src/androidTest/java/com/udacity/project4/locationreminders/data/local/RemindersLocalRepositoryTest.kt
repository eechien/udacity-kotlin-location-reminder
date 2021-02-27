package com.udacity.project4.locationreminders.data.local

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var remindersDatabase: RemindersDatabase
    private lateinit var remindersDAO: RemindersDao
    private lateinit var repository: RemindersLocalRepository

//    TODO: Add testing implementation to the RemindersLocalRepository.kt
    @Before
    fun setup() {
        remindersDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().context,
                RemindersDatabase::class.java
        )
                .allowMainThreadQueries()
                .build()
        remindersDAO = remindersDatabase.reminderDao()
        repository =
                RemindersLocalRepository(
                        remindersDAO
                )
    }

    @After
    fun tearDown() {
        remindersDatabase.close()
    }

    @Test
    fun saveReminderAndGetReminder() = runBlocking {
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )
        repository.saveReminder(reminder)

        val result = repository.getReminder(reminder.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun saveRemindersAndGetReminders() = runBlocking {
        val reminder1 = ReminderDTO(
                "title1",
                "description1",
                "location1",
                1.01,
                1.02
        )
        val reminder2 = ReminderDTO(
                "title2",
                "description2",
                "location2",
                2.01,
                2.02
        )
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)

        val result = repository.getReminders()
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        val remindersResultList = result.data
        assertThat(remindersResultList.size, `is`(2))
        assertThat(remindersResultList.contains(reminder1), `is`(true))
        assertThat(remindersResultList.contains(reminder2), `is`(true))
    }

    @Test
    fun saveReminderAndDeleteAllReminders() = runBlocking {
        val reminder1 = ReminderDTO(
                "title1",
                "description1",
                "location1",
                1.01,
                1.02
        )
        val reminder2 = ReminderDTO(
                "title2",
                "description2",
                "location2",
                2.01,
                2.02
        )
        repository.saveReminder(reminder1)
        repository.saveReminder(reminder2)

        repository.deleteAllReminders()
        val result = repository.getReminders()
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        val remindersResultList = result.data
        assertThat(remindersResultList.size, `is`(0))
    }

}