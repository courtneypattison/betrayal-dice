package com.courtneypattison.betrayaldice

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        mainViewModel = MainViewModel()
    }

    @Test
    fun onRollDice_player0ScoreSet() {
        mainViewModel.onRollDice()
         val value = mainViewModel.player0Score.getOrAwaitValue()
        assertThat(value, not(nullValue()))
    }
}