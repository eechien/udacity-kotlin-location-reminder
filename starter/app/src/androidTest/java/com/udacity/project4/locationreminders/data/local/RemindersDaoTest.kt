package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun saveReminderAndGetById() = runBlockingTest {
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )
        database.reminderDao().saveReminder(reminder)

        val loaded = database.reminderDao().getReminderById(reminder.id)
        assertThat<ReminderDTO>(loaded as ReminderDTO, notNullValue())
        assertThat(loaded.id, `is`(reminder.id))
        assertThat(loaded.title, `is`(reminder.title))
        assertThat(loaded.description, `is`(reminder.description))
        assertThat(loaded.latitude, `is`(reminder.latitude))
        assertThat(loaded.longitude, `is`(reminder.longitude))
    }

    @Test
    fun saveReminderAndDeleteAllReminders() = runBlockingTest {
        val reminder = ReminderDTO(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )
        database.reminderDao().saveReminder(reminder)
        database.reminderDao().deleteAllReminders()

        val loadedReminders = database.reminderDao().getReminders()
        assertThat(loadedReminders.isEmpty(), `is`(true))
    }

    @Test
    fun saveReminderAndGetReminders() = runBlockingTest {
        val reminder1 = ReminderDTO(
                "title1",
                "description1",
                "location1",
                1.00,
                2.00
        )
        val reminder2 = ReminderDTO(
                "title2",
                "description2",
                "location2",
                2.00,
                3.00
        )
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        val loadedReminders = database.reminderDao().getReminders()
        assertThat(loadedReminders.size, `is`(2))
    }
}