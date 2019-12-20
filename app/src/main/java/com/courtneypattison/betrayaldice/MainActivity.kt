package com.courtneypattison.betrayaldice

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var omenCardCount = 0
    private lateinit var selectorImageViews: Array<ImageView>
    private var selectorYs = arrayOf(0f, 0f)
    private var selectedButtons = arrayOfNulls<View>(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.selectorImageViews = arrayOf(player0SelectorImageView, player1SelectorImageView)
    }

    // onClick Functions

    fun hauntRoll(view: View) {
        val sum = rollNDice(6)

        if (sum < omenCardCount) beginHaunt() else incrementOmenCardCount()
    }

    fun newGame(view: View) {
        moveSelector(selectorImageViews[0], 0)
        moveSelector(selectorImageViews[1], 1)

        this.omenCardCount = 0
        this.selectedButtons = arrayOfNulls(2)

        hideDamage()

        player0SumTextView.text = getString(R.string.zero)
        player1SumTextView.text = getString(R.string.zero)

        omenCardCountTextView.text = this.omenCardCount.toString()

        show(hauntRollButton)
        show(omenCardCountTextView)
    }

    fun rollDice(view: View) {
        highlightButton(view)
        setPlayerScore(getPlayerNumber(view), rollNDice(getDieCount(view)))
        updateDamage()
    }

//    Private functions

    private fun getDieCount(view: View): Int {
        return view.tag.toString().last().toString().toInt()
    }

    private fun getPlayerNumber(view: View): Int {
        return view.tag.toString().first().toString().toInt()
    }

    private fun getPlayerViewId(playerNumber: Int): TextView {
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
        hide(hauntRollButton)
        hide(omenCardCountTextView)
    }

    private fun highlightButton(view: View) {
        val playerNumber = getPlayerNumber(view)
        this.selectedButtons[playerNumber] = view
        moveSelector(view, playerNumber)
    }

    private fun moveSelector(view: View, playerNumber: Int) {
        if (this.selectorYs[playerNumber] == 0f) this.selectorYs[playerNumber] = selectorImageViews[playerNumber].y
        val difference = view.y - this.selectorYs[playerNumber]

        ObjectAnimator.ofFloat(selectorImageViews[playerNumber], "translationY", difference).apply {
            duration = 2000
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
        getPlayerViewId(playerNumber).text = score.toString()
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
