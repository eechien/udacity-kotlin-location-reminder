package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
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

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersDataSource: FakeDataSource
    private lateinit var remindersListViewModel: RemindersListViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        remindersDataSource = FakeDataSource()
        remindersListViewModel = RemindersListViewModel(
                ApplicationProvider.getApplicationContext(),
                remindersDataSource
        )
    }

    @After
    fun tearDownViewModel() {
        stopKoin()
    }

    @Test
    fun loadReminders_updatesShowLoading() {
        mainCoroutineRule.pauseDispatcher()
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showLoading.value, `is`(true))
        mainCoroutineRule.resumeDispatcher()
        assertThat(remindersListViewModel.showLoading.value, `is`(false))
    }

    @Test
    fun loadReminders_showNoDataFalse() = runBlockingTest {
        remindersDataSource.saveReminder(ReminderDTO(
                "title",
                "description",
                "location",
                1.00,
                2.00
        ))

        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showNoData.value, `is`(false))
    }

    @Test
    fun loadReminders_showNoDataTrue() {
        remindersListViewModel.loadReminders()
        assertThat(remindersListViewModel.showNoData.value, `is`(true))
    }
}