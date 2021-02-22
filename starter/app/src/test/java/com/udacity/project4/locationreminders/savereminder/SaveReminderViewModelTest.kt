package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.succeeded
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    private lateinit var remindersDataSource: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        remindersDataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(
                ApplicationProvider.getApplicationContext(),
                remindersDataSource
        )
    }

    @After
    fun tearDownViewModel() {
        stopKoin()
    }

    @Test
    fun saveReminder_valuesUpdated() {
        mainCoroutineRule.pauseDispatcher()
        val reminder = ReminderDataItem(
            "title",
            "description",
            "location",
            1.00,
            2.00
        )
        saveReminderViewModel.saveReminder(reminder)

        assertThat(saveReminderViewModel.showLoading.value, `is`(true))
        mainCoroutineRule.resumeDispatcher()

        assertThat(saveReminderViewModel.showLoading.value, `is`(false))
        assertThat(saveReminderViewModel.showToast.value, `is`("Reminder Saved !"))
    }

    @Test
    fun saveReminder_savesReminder() = runBlockingTest {
        val reminder = ReminderDataItem(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )
        saveReminderViewModel.saveReminder(reminder)

        val result = remindersDataSource.getReminder(reminder.id)
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.description, `is`(reminder.description))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))

    }

    @Test
    fun validateEnteredData_emptyTitle_returnsFalse() {
        val reminder = ReminderDataItem(
            "",
            "description",
            "location",
            1.00,
            2.00
        )
        val result = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(result, `is`(false))
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_nullTitle_returnsFalse() {
        val reminder = ReminderDataItem(
                null,
                "description",
                "location",
                1.00,
                2.00
        )
        val result = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(result, `is`(false))
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_enter_title))
    }

    @Test
    fun validateEnteredData_emptyLocation_returnsFalse() {
        val reminder = ReminderDataItem(
                "title",
                "description",
                "",
                1.00,
                2.00
        )
        val result = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(result, `is`(false))
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun validateEnteredData_nullLocation_returnsFalse() {
        val reminder = ReminderDataItem(
                "title",
                "description",
                null,
                1.00,
                2.00
        )
        val result = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(result, `is`(false))
        assertThat(saveReminderViewModel.showSnackBarInt.value, `is`(R.string.err_select_location))
    }

    @Test
    fun validateEnteredData_returnsTrue() {
        val reminder = ReminderDataItem(
                "title",
                "description",
                "location",
                1.00,
                2.00
        )
        val result = saveReminderViewModel.validateEnteredData(reminder)
        assertThat(result, `is`(true))
    }

}