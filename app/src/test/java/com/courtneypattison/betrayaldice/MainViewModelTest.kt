package com.courtneypattison.betrayaldice

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.collect.Range
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.P)
@RunWith(AndroidJUnit4::class)
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel
    private val maxDieValue = 2

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        mainViewModel = MainViewModel()
    }

    @Test
    fun onAttack_setPrevScore() {
        mainViewModel.onAttack()
        val player0Score = mainViewModel.player0Score.getOrAwaitValue()
        mainViewModel.onAttack()
        val player0ScorePrev = mainViewModel.player0ScorePrev.getOrAwaitValue()
        assertThat(player0Score).isEqualTo(player0ScorePrev)
    }

    @Test
    fun onAttack_setScore() {
        mainViewModel.onAttack()
        val player0Score = mainViewModel.player0Score.getOrAwaitValue()
        val player0DieCount = mainViewModel.player0DieCount.getOrAwaitValue()
        assertThat(player0Score).isIn(Range.closed(0, player0DieCount * maxDieValue))
    }

    @Test
    fun onHauntRoll_BeginHauntOrIncrementOmenCardCount() {
        mainViewModel.onHauntRoll()
        val omenCardCount = mainViewModel.omenCardCount.getOrAwaitValue()
        val isHaunt = mainViewModel.isHaunt.getOrAwaitValue()
        if (isHaunt) {
            assertThat(isHaunt).isTrue()
        } else {
            assertThat(omenCardCount).isEqualTo(1)
        }
    }

    @Test
    fun onNewGame_setDefaultValues() {
        mainViewModel.onHauntRoll()
        var omenCardCount = mainViewModel.omenCardCount.getOrAwaitValue()
        assertThat(omenCardCount).isEqualTo(1)
        mainViewModel.onNewGame()
        omenCardCount = mainViewModel.omenCardCount.getOrAwaitValue()
        assertThat(omenCardCount).isEqualTo(0)
    }

    @Test
    fun onRollDice_player0ScoreSet() {
        mainViewModel.onRollDice()
        val player0Score = mainViewModel.player0Score.getOrAwaitValue()
        val maxDieCount = 8
        assertThat(player0Score).isIn(Range.closed(0,  maxDieCount * maxDieValue))
    }

    @Test
    fun onRollDice_player0ScorePrevSet() {
        mainViewModel.onRollDice()
        val player0Score = mainViewModel.player0Score.getOrAwaitValue()
        mainViewModel.onRollDice()
        val player0ScorePrev = mainViewModel.player0ScorePrev.getOrAwaitValue()

        assertThat(player0ScorePrev).isEqualTo(player0Score)
    }
}