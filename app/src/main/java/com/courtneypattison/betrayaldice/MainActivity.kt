package com.courtneypattison.betrayaldice

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var isHaunt = false
    private var omenCardCount = 0
    private lateinit var player1Views: Array<View>
    private lateinit var selectedDieCountButtons: Array<Button>
    private lateinit var selectorImageViews: Array<ImageView>
    private var selectorYs = arrayOf(0f, 0f)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.player1Views = arrayOf(
            player1SumTextView,
            player1SelectorImageView,
            player1Die1Button,
            player1Die2Button,
            player1Die3Button,
            player1Die4Button,
            player1Die5Button,
            player1Die6Button,
            player1Die7Button,
            player1Die8Button
        )
        this.selectedDieCountButtons = arrayOf(player0Die1Button, player1Die1Button)
        this.selectorImageViews = arrayOf(player0SelectorImageView, player1SelectorImageView)
    }

    // onClick Functions

    fun hauntRoll(view: View) {
        val sum = rollNDice(6)

        if (sum < omenCardCount) beginHaunt() else incrementOmenCardCount()
    }

    fun newGame(view: View) {
        moveSelector(player0Die1Button, 0)
        moveSelector(player1Die1Button, 1)

        this.isHaunt = false
        this.omenCardCount = 0
        this.selectedDieCountButtons = arrayOf(player0Die1Button, player1Die1Button)

        for (player1View in this.player1Views) hide(player1View)

        hideDamage()

        player0SumTextView.text = getString(R.string.zero)
        player1SumTextView.text = getString(R.string.zero)

        omenCardCountTextView.text = this.omenCardCount.toString()

        show(hauntRollButton)
        show(omenCardCountTextView)
    }

    fun rollDice(view: View) {
        setPlayerScore(0, rollNDice(getDieCount(selectedDieCountButtons[0])))

        if (this.isHaunt) {
            setPlayerScore(1, rollNDice(getDieCount(selectedDieCountButtons[1])))
            updateDamage()
        }
    }

    fun selectNDice(view: View) {
        val playerNumber = getPlayerNumber(view)
        this.selectedDieCountButtons[playerNumber] = view as Button
        moveSelector(view, playerNumber)
    }

//    Private functions

    private fun getDieCount(view: View): Int {
        return view.tag.toString().last().toString().toInt()
    }

    private fun getPlayerNumber(view: View): Int {
        return view.tag.toString().first().toString().toInt()
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

    private fun beginHaunt() {
        this.isHaunt = true
        hide(hauntRollButton)
        hide(omenCardCountTextView)

        for (player1View in this.player1Views) fadeIn(player1View)
        fadeIn(hauntBeginsTextView)
        fadeOut(hauntBeginsTextView, 3000)
    }

    private fun fadeIn(view: View, delay: Long = 0, length: Long = 1000) {
        show(view)
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = length
            startDelay = delay
            start()
        }
    }

    private fun fadeOut(view: View, delay: Long = 0, length: Long = 10000) {
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
            duration = length
            startDelay = delay
            start()
        }
    }

    private fun moveSelector(view: View, playerNumber: Int) {
        if (this.selectorYs[playerNumber] == 0f) this.selectorYs[playerNumber] = selectorImageViews[playerNumber].y + 7f
        val difference = view.y - this.selectorYs[playerNumber]

        ObjectAnimator.ofFloat(selectorImageViews[playerNumber], "translationY", difference).apply {
            duration = 1000
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
        textView.text = damage.toString()

        hideDamage()
        show(textView)
    }

    private fun setPlayerScore(playerNumber: Int, score: Int) {
        val sumTextView = getPlayerSumTextView(playerNumber)

        fadeOut(sumTextView)
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
