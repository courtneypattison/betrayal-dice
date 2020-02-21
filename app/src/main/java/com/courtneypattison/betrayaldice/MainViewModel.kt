package com.courtneypattison.betrayaldice

import android.util.Log
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

    // The number of dice player 1 will roll
    private var _player1DieCount = MutableLiveData<Int>()

    // Score from roll for player 0
    private var _player0Score = MutableLiveData<Int>()
    val player0Score: LiveData<Int>
        get() = _player0Score

    // Score from roll for player 1
    private var _player1Score = MutableLiveData<Int>()
    val player1Score: LiveData<Int>
        get() = _player1Score

    init {
        setDefaultValues()
    }

    fun beginHauntComplete() {
        _eventHaunt.value = false
    }

    fun setDieCount(playerNumber: Int, dieCount: Int) {
        if (playerNumber == 0) _player0DieCount.value = dieCount else _player1DieCount.value = dieCount
    }

    /** onClick methods **/

    fun onAttack() {
        _player0Score.value = rollNDice(_player0DieCount.value!!)
        _player1Score.value = rollNDice(_player1DieCount.value!!)
        updateDamage()
    }

    fun onHauntRoll() {
        if (rollNDice(6) < _omenCardCount.value!!) beginHaunt() else incrementOmenCardCount()
    }

    fun onNewGame() {
        setDefaultValues()
    }

    fun onRollDice() {
        _player0Score.value = rollNDice(_player0DieCount.value!!)
    }

    /** Private methods **/

    private fun beginHaunt() {
        _eventHaunt.value = true
    }

    private fun incrementOmenCardCount() {
        _omenCardCount.value = (_omenCardCount.value)?.plus(1)
    }

    private fun setDefaultValues() {
        _eventHaunt.value = false
        _eventTie.value = false
        _omenCardCount.value = 0
        _player0Damage.value = 0
        _player1Damage.value = 0
        _player0DieCount.value = 1
        _player1DieCount.value = 1
        _player0Score.value = 0
        _player1Score.value = 0
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