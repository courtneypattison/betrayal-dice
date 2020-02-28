package com.courtneypattison.betrayaldice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random


class MainViewModel : ViewModel() {

    // Event that triggers the haunt
    private var _eventHaunt = MutableLiveData<Boolean>()
    val eventHaunt: LiveData<Boolean>
        get() = _eventHaunt

    // Event that triggers a tie when scores are equal
    private var _eventTie = MutableLiveData<Boolean>()
    val eventTie: LiveData<Boolean>
        get() = _eventTie

    // If it is currently the haunt
    private var _isHaunt = MutableLiveData<Boolean>()
    val isHaunt: LiveData<Boolean>
        get() = _isHaunt

    // The number of omen cards revealed
    private var _omenCardCount = MutableLiveData<Int>()
    val omenCardCount: LiveData<Int>
        get() = _omenCardCount

    // The amount of damage player 0 sustains
    private var _player0Damage = MutableLiveData<Int>()
    val player0Damage: LiveData<Int>
        get() = _player0Damage

    // The amount of damage player 1 sustains
    private var _player1Damage = MutableLiveData<Int>()
    val player1Damage: LiveData<Int>
        get() = _player1Damage

    // The number of dice player 0 will roll
    private var _player0DieCount = MutableLiveData<Int>()
    val player0DieCount: LiveData<Int>
        get() = _player0DieCount

    // The number of dice player 1 will roll
    private var player1DieCount = MutableLiveData<Int>()

    // Score from roll for player 0
    private var _player0Score = MutableLiveData<Int>()
    val player0Score: LiveData<Int>
        get() = _player0Score

    // Score from previous roll for player 0
    private var _player0ScorePrev = MutableLiveData<Int>()
    val player0ScorePrev: LiveData<Int>
        get() = _player0ScorePrev

    // Score from roll for player 1
    private var _player1Score = MutableLiveData<Int>()
    val player1Score: LiveData<Int>
        get() = _player1Score

    // Score from previous roll for player 1
    private var _player1ScorePrev = MutableLiveData<Int>()
    val player1ScorePrev: LiveData<Int>
        get() = _player1ScorePrev

    val outcomeProbabilities = arrayOf(
        floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        floatArrayOf(33.3f, 33.3f, 33.3f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        floatArrayOf(11f, 22f, 33f, 22f, 11f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        floatArrayOf(4f, 11f, 22f, 26f, 22f, 11f, 4f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        floatArrayOf(1f, 5f, 12f, 20f, 23f, 20f, 12f, 5f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        floatArrayOf(0f, 2f, 6f, 12f, 19f, 21f, 19f, 12f, 6f, 2f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        floatArrayOf(0f, 1f, 3f, 7f, 12f, 17f, 19f, 17f, 12f, 7f, 3f, 1f, 0f, 0f, 0f, 0f, 0f),
        floatArrayOf(0f, 0f, 1f, 4f, 7f, 12f, 16f, 18f, 16f, 12f, 7f, 4f, 1f, 0f, 0f, 0f, 0f),
        floatArrayOf(0f, 0f, 1f, 2f, 4f, 8f, 12f, 15f, 17f, 15f, 12f, 8f, 4f, 2f, 1f, 0f, 0f)
    )

    init {
        setDefaultValues()
    }

    fun beginHauntComplete() {
        _eventHaunt.value = false
    }

    fun setDieCount(playerNumber: Int, dieCount: Int) {
        if (playerNumber == 0) _player0DieCount.value = dieCount else player1DieCount.value = dieCount
    }

    /** onClick methods **/

    fun onAttack() {
        _player0ScorePrev.value = _player0Score.value
        _player1ScorePrev.value = _player1Score.value

        _player0Score.value = rollNDice(player0DieCount.value!!)
        _player1Score.value = rollNDice(player1DieCount.value!!)

        updateDamage()
    }

    fun onHauntRoll() {
        if (rollNDice(6) < _omenCardCount.value!!) beginHaunt() else incrementOmenCardCount()
    }

    fun onNewGame() {
        setDefaultValues()
    }

    fun onRollDice() {
        _player0ScorePrev.value = _player0Score.value
        _player0Score.value = rollNDice(player0DieCount.value!!)
    }

    /** Private methods **/

    private fun beginHaunt() {
        _eventHaunt.value = true
        _isHaunt.value = true
    }

    private fun incrementOmenCardCount() {
        _omenCardCount.value = (_omenCardCount.value)?.plus(1)
    }

    private fun setDefaultValues() {
        _eventHaunt.value = false
        _eventTie.value = false
        _isHaunt.value = false
        _omenCardCount.value = 0
        _player0Damage.value = 0
        _player1Damage.value = 0
        _player0DieCount.value = 1
        player1DieCount.value = 1
        _player0Score.value = 0
        _player0ScorePrev.value = 0
        _player1Score.value = 0
        _player1ScorePrev.value = 0
    }

    private fun rollDie(): Int {
        return Random.nextInt(3)
    }

    private fun rollNDice(n: Int): Int {
        var sum = 0
        for (i in 1..n) {
            sum += rollDie()
        }
        return sum
    }

    private fun updateDamage() {
        val difference = _player0Score.value!! - _player1Score.value!!

        if (difference < 0) {
            _eventTie.value = false
            _player0Damage.value = difference
            _player1Damage.value = 0
        } else if (difference > 0) {
            _eventTie.value = false
            _player0Damage.value = 0
            _player1Damage.value = -difference
        } else {
            _eventTie.value = true
        }
    }
}