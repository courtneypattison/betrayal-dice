package com.courtneypattison.betryaldice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var omenCardCount = 0
    private var player1SelectedButtonId = 0
    private var player2SelectedButtonId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun hauntRoll(view: View) {
        val sum = rollNDice(6)
        this.omenCardCount++

        if (sum < omenCardCount) {
            getHauntRollView().visibility = View.INVISIBLE
            getOmenCardCountView().visibility = View.INVISIBLE
            getOmenCardCountLabelView().text = getString(R.string.haunt)
        } else {
            getOmenCardCountView().text = this.omenCardCount.toString()
        }
    }

    fun rollDice(view: View) {
        highlightDieCount(view)
        setPlayerScore(getPlayerNumber(view), rollNDice(getDieCount(view)))
        updateDifference()
    }

//    Private functions

    private fun getButtonView(id: Int): Button {
        return findViewById<Button>(id)
    }

    private fun getDieCount(view: View): Int {
        return view.tag.toString().last().toString().toInt();
    }

    private fun getHauntRollView(): Button {
        return findViewById<Button>(R.id.hauntRoll)
    }

    private fun getOmenCardCountView(): TextView {
        return findViewById<TextView>(R.id.omenCardCount)
    }

    private fun getOmenCardCountLabelView(): TextView {
        return findViewById<TextView>(R.id.omenCardCountLabel)
    }

    private fun getPlayerNumber(view: View): Int {
        return view.tag.toString().first().toString().toInt()
    }

    private fun getPlayerViewId(playerNumber: Int): Int {
        return if (playerNumber == 1) R.id.player1sum else R.id.player2sum
    }

    private fun getPlayerScore(playerNumber: Int): Int? {
        return findViewById<TextView>(getPlayerViewId(playerNumber)).text.toString().toIntOrNull()
    }

    private fun getPrevSelectedButtonId(playerNumber: Int): Int {
        return if (playerNumber == 1) player1SelectedButtonId else player2SelectedButtonId
    }

    private fun highlightDieCount(view: View) {
        val playerNumber = getPlayerNumber(view)
        val prevSelectedButtonId = getPrevSelectedButtonId(playerNumber)

        setButtonTintToPrimaryColor(prevSelectedButtonId)
        setSelectedButtonId(playerNumber, view.id)
        setButtonTintToAccentColor(view.id)
    }

    private fun setButtonTintToPrimaryColor(id: Int) {
        if (id != 0) {
            DrawableCompat.setTint(getButtonView(id).background, getColor(R.color.colorPrimary))
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

    private fun setButtonTintToAccentColor(id: Int) {
        val buttonView = getButtonView(id)
        DrawableCompat.setTint(buttonView.background, getColor(R.color.colorAccent))
    }

    private fun setPlayerScore(playerNumber: Int, score: Int) {
        findViewById<TextView>(getPlayerViewId(playerNumber)).text = score.toString()
    }

    private fun setSelectedButtonId(playerNumber: Int, id: Int) {
        if (playerNumber == 1) {
            player1SelectedButtonId = id
        } else {
            player2SelectedButtonId = id
        }
    }

    private fun updateDifference() {
        var difference = (getPlayerScore(1) ?: 0) - (getPlayerScore(2) ?: 0)
        findViewById<TextView>(R.id.playerDifference).text = difference.toString()
    }
}
