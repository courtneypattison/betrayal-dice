package com.courtneypattison.betrayaldice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random

enum class RollType {
    ROLL, ATTACK, HAUNT
}

class MainViewModel : ViewModel() {

    // Event that triggers the haunt
    private var _eventHaunt = MutableLiveData<Boolean>()
    val eventHaunt: LiveData<Boolean>
        get() = _eventHaunt

    // Event that triggers a tie when scores are equal
    private var _eventTie = MutableLiveData<Boolean>()
    val eventTie: LiveData<Boolean>
        get() = _eventTie

    // Result of haunt roll
    private var _hauntRollResult = MutableLiveData<Int>()
    val hauntRollResult: LiveData<Int>
        get() = _hauntRollResult

    // If it is currently the haunt
    private var _isHaunt = MutableLiveData<Boolean>()
    val isHaunt: LiveData<Boolean>
        get() = _isHaunt

    // The number of omen cards revealed
    private var _omenCardCount = MutableLiveData<Int>()
    val omenCardCount: LiveData<Int>
        get() = _omenCardCount

    // The amount of damage player 0 sustains
    private var _player0Damage = MutableLiveData<Int?>()
    val player0Damage: LiveData<Int?>
        get() = _player0Damage

    // The amount of damage player 1 sustains
    private var _player1Damage = MutableLiveData<Int?>()
    val player1Damage: LiveData<Int?>
        get() = _player1Damage

    // The number of dice player 0 will roll
    private var _player0DieCount = MutableLiveData<Int>()
    val player0DieCount: LiveData<Int>
        get() = _player0DieCount

    // The number of dice player 1 will roll
    private var player1DieCount = MutableLiveData<Int>()

    // Score from roll for player 0
    private var _player0Score = MutableLiveData<Int?>()
    val player0Score: LiveData<Int?>
        get() = _player0Score

    // Score from previous roll for player 0
    private var _player0ScorePrev = MutableLiveData<Int?>()
    val player0ScorePrev: LiveData<Int?>
        get() = _player0ScorePrev

    // Score from roll for player 1
    private var _player1Score = MutableLiveData<Int?>()
    val player1Score: LiveData<Int?>
        get() = _player1Score

    // Score from previous roll for player 1
    private var _player1ScorePrev = MutableLiveData<Int?>()
    val player1ScorePrev: LiveData<Int?>
        get() = _player1ScorePrev

    // Values of each die rolled for player 0
    private var _rollDiceValues = MutableLiveData<String>()
    val rollDiceValues: LiveData<String>
        get() = _rollDiceValues

    // Values of each die rolled for player 1
    private var _attackDiceValues = MutableLiveData<String?>()
    val attackDiceValues: LiveData<String?>
        get() = _attackDiceValues

    // Values of each die rolled for haunt roll
    private var _hauntRollDiceValues = MutableLiveData<String?>()
    val hauntRollDiceValues: LiveData<String?>
        get() = _hauntRollDiceValues

    val outcomeProbabilities = arrayOf(
        intArrayOf(0),
        intArrayOf(33, 33, 33, 0),
        intArrayOf(11, 22, 33, 22, 11),
        intArrayOf(4, 11, 22, 26, 22, 11, 4),
        intArrayOf(1, 5, 12, 20, 23, 20, 12, 5, 1),
        intArrayOf(0, 2, 6, 12, 19, 21, 19, 12, 6, 2, 0),
        intArrayOf(0, 1, 3, 7, 12, 17, 19, 17, 12, 7, 3, 1, 0),
        intArrayOf(0, 0, 1, 4, 7, 12, 16, 18, 16, 12, 7, 4, 1, 0, 0),
        intArrayOf(0, 0, 1, 2, 4, 8, 12, 15, 17, 15, 12, 8, 4, 2, 1, 0, 0)
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

        _player0Score.value = rollNDice(player0DieCount.value!!, RollType.ROLL)
        _player1Score.value = rollNDice(player1DieCount.value!!, RollType.ATTACK)

        updateDamage()
    }

    fun onHauntRoll(hauntRollType: Int) {
        incrementOmenCardCount()
        when (hauntRollType) {
            0 -> hauntRollOriginal()
            1 -> hauntRollLegacy()
            2 -> hauntRollUnofficial()
        }
    }

    fun onNewGame() {
        setDefaultValues()
    }

    fun onRollDice() {
        _player0ScorePrev.value = _player0Score.value
        _player0Score.value = rollNDice(player0DieCount.value!!, RollType.ROLL)
    }

    /** Private methods **/

    private fun beginHaunt() {
        _eventHaunt.value = true
        _isHaunt.value = true
    }

    private fun hauntRollLegacy() {
        hauntRollNewRules(6)
    }

    private fun hauntRollOriginal() {
        _hauntRollResult.value = rollNDice(6, RollType.HAUNT)
        if (_hauntRollResult.value!! < _omenCardCount.value!!) {
            beginHaunt()
        }
    }

    private fun hauntRollNewRules(n: Int) {
        _hauntRollResult.value = rollNDice(_omenCardCount.value!!, RollType.HAUNT)
        if (_hauntRollResult.value!! >= n) {
            beginHaunt()
        }
    }

    private fun hauntRollUnofficial() {
        hauntRollNewRules(7)
    }

    private fun incrementOmenCardCount() {
        _omenCardCount.value = (_omenCardCount.value)?.plus(1)
    }

    private fun setDefaultValues() {
        _eventHaunt.value = false
        _eventTie.value = false
        _isHaunt.value = false
        _omenCardCount.value = 0
        _player0Damage.value = null
        _player1Damage.value = null
        _player0DieCount.value = 1
        player1DieCount.value = 1
        _player0Score.value = null
        _player0ScorePrev.value = null
        _player1Score.value = null
        _player1ScorePrev.value = null
        _rollDiceValues.value = null
        _attackDiceValues.value = null
        _hauntRollDiceValues.value = null
    }

    private fun rollDie(): Int {
        return Random.nextInt(3)
    }

    private fun rollNDice(n: Int, rollType: RollType): Int {
        var sum = 0
        val builder = StringBuilder()

        for (i in 1..n) {
            val num = rollDie()
            sum += num
            builder.append(num).append(" ")
        }

        when (rollType) {
            RollType.ROLL -> _rollDiceValues.value = builder.toString()
            RollType.ATTACK -> _attackDiceValues.value = builder.toString()
            RollType.HAUNT -> _hauntRollDiceValues.value = builder.toString()
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