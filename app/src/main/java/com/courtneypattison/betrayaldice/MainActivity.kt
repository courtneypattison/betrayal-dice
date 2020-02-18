package com.courtneypattison.betrayaldice

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var isHaunt = false
    private var omenCardCount = 0
    private var playerDieCount = arrayOf(0, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setNumberPicker(player0NumberPicker, 0)
        this.setNumberPicker(player1NumberPicker, 1)
    }

    // onClick Functions

    fun attack(view: View) {
        setPlayerScore(0, rollNDice(playerDieCount[0]))
        setPlayerScore(1, rollNDice(playerDieCount[1]))
        updateDamage()
    }

    fun hauntRoll(view: View) {
        val sum = rollNDice(6)

        if (sum < omenCardCount) beginHaunt() else incrementOmenCardCount()
    }

    fun newGame(view: View) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
            .setTitle("New Game")
            .setMessage("Are you sure you want to start a new game?")
            .setCancelable(true)
            .setPositiveButton("New Game") { _, _ -> resetGame() }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
            .show()
    }

    fun rollDice(view: View) {
        hideDamage()
        setPlayerScore(0, rollNDice(playerDieCount[0]))
    }

//    Private functions

    private fun setNumberPicker(numberPicker: NumberPicker, playerNumber: Int) {
        numberPicker.maxValue = 8
        numberPicker.minValue = 1
        numberPicker.setOnValueChangedListener {_, _, newVal -> playerDieCount[playerNumber] = newVal}
    }

    private fun resetGame() {
        this.isHaunt = false
        this.omenCardCount = 0
        this.playerDieCount = arrayOf(0, 0)

        hideDamage()

        player0SumTextView.text = getString(R.string.zero)
        player1SumTextView.text = getString(R.string.zero)

        omenCardCountTextView.text = this.omenCardCount.toString()

        setButtonColors(getColor(R.color.colorPrimary))

        show(hauntRollButton)
        show(omenCardCountTextView)
    }

    private fun getPlayerSumTextView(playerNumber: Int): TextView {
        return if (playerNumber == 0) player0SumTextView else player1SumTextView
    }

    private fun getPlayerScore(playerSumTextView: TextView): Int? {
        return playerSumTextView.text.toString().toIntOrNull()
    }

    private fun incrementOmenCardCount() {
        this.omenCardCount++
        omenCardCountTextView.text = this.omenCardCount.toString()
    }

    private fun setTextColor(button: Button, color: Int) {
        button.setTextColor(color)
    }

    private fun setButtonColors(color: Int) {
        setTextColor(rollDiceButton, color)
        setTextColor(attackButton, color)
        setTextColor(newGameButton, color)
    }

    private fun beginHaunt() {
        this.isHaunt = true
        hide(hauntRollButton)
        hide(omenCardCountTextView)

        fadeOut(constraintLayout)
        Handler().postDelayed({
            setButtonColors(getColor(R.color.colorPrimaryHaunt))
            show(hauntBeginsTextView)
        }, 2000)

        fadeIn(constraintLayout, 2000)
        fadeOut(hauntBeginsTextView, 6000)
    }

    private fun fadeIn(view: View, delay: Long = 0, length: Long = 1000) {
        show(view)
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = length
            startDelay = delay
            start()
        }
    }

    private fun fadeOut(view: View, delay: Long = 0, length: Long = 1000) {
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
            duration = length
            startDelay = delay
            start()
        }
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

    private fun setDamage(textView: TextView, damage: Int) {
        hideDamage()
        textView.text = damage.toString()
        fadeIn(textView)
    }

    private fun setPlayerScore(playerNumber: Int, score: Int) {
        val sumTextView = getPlayerSumTextView(playerNumber)

        hide(sumTextView)
        sumTextView.text = score.toString()
        fadeIn(sumTextView)
    }

    private fun show(view: View) {
        view.visibility = View.VISIBLE
    }

    private fun hide(view: View) {
        view.visibility = View.INVISIBLE
    }

    private fun hideDamage() {
        hide(player0DamageTextView)
        hide(player1DamageTextView)
        hide(playerTieTextView)
    }

    private fun updateDamage() {
        val difference = (getPlayerScore(player0SumTextView) ?: 0) - (getPlayerScore(player1SumTextView) ?: 0)

        when {
            (difference < 0) -> setDamage(player0DamageTextView, difference)
            (difference > 0) -> setDamage(player1DamageTextView, -difference)
            else -> setDamage(playerTieTextView, 0)

        }
    }
}
