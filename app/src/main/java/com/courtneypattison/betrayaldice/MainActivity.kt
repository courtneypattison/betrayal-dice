package com.courtneypattison.betrayaldice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.random.Random
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var omenCardCount = 0
    private var selectedButtons = arrayOfNulls<View>(2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // onClick Functions

    fun hauntRoll(view: View) {
        val sum = rollNDice(6)

        if (sum < omenCardCount) beginHaunt() else incrementOmenCardCount()
    }

    fun newGame(view: View) {
        styleAsUnselectedButton(this.selectedButtons[0])
        styleAsUnselectedButton(this.selectedButtons[1])

        this.omenCardCount = 0
        this.selectedButtons = arrayOfNulls(2)

        hideDamage()

        player0SumTextView.text = getString(R.string.zero)
        player1SumTextView.text = getString(R.string.zero)

        omenCardCountLabelTextView.text = getString(R.string.omen_card_count_label)
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
        omenCardCountLabelTextView.text = getString(R.string.haunt)
    }

    private fun highlightButton(view: View) {
        val playerNumber = getPlayerNumber(view)
        styleAsUnselectedButton(this.selectedButtons[playerNumber])
        this.selectedButtons[playerNumber] = view
        styleAsSelectedButton(view)
    }

    private fun styleAsUnselectedButton(view: View?) {
        if (view != null) DrawableCompat.setTint(view.background, getColor(R.color.colorPrimary))
    }

    private fun styleAsSelectedButton(view: View) {
        DrawableCompat.setTint(view.background, getColor(R.color.colorAccent))
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
