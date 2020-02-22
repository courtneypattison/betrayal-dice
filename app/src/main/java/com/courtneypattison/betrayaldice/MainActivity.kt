package com.courtneypattison.betrayaldice

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private var alertDialogTheme = R.style.MaterialAlertDialogCustom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setNumberPickerValues(player0NumberPicker, 0)
        this.setNumberPickerValues(player1NumberPicker, 1)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.eventHaunt.observe(this, Observer<Boolean> { eventHaunt ->
            if (eventHaunt) beginHaunt()
        })

        viewModel.eventTie.observe(this, Observer<Boolean> { isTie ->
            if (isTie) fadeIn(tieTextView)
        })

        viewModel.isHaunt.observe(this, Observer<Boolean> { isHaunt ->
            if (isHaunt) styleHaunt()
        })

        viewModel.omenCardCount.observe(this, Observer<Int> { omenCardCount ->
            omenCardCountTextView.text = omenCardCount.toString()
        })

        viewModel.player0Damage.observe(this, Observer<Int> { damage ->
            updateDamage(player0DamageTextView, damage)
        })

        viewModel.player1Damage.observe(this, Observer<Int> { damage ->
            updateDamage(player1DamageTextView, damage)
        })

        viewModel.player0Score.observe(this, Observer<Int> { score ->
            player0ScoreTextView.text = score.toString()
            fadeIn(player0ScoreTextView)
        })

        viewModel.player1Score.observe(this, Observer<Int> { score ->
            player1ScoreTextView.text = score.toString()
            fadeIn(player1ScoreTextView)
        })
    }

    /** onClick methods **/

    fun onAttack(view: View) {
        hideDamage()
        viewModel.onAttack()
    }

    fun onHauntRoll(view: View) {
        viewModel.onHauntRoll()
    }

    fun onNewGame(view: View) {
        MaterialAlertDialogBuilder(this, alertDialogTheme)
            .setTitle("New Game")
            .setMessage("Are you sure you want to start a new game?")
            .setCancelable(true)
            .setPositiveButton("New Game") { _, _ -> onNewGameConfirm() }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
            .show()
    }

    private fun onNewGameConfirm() {
        viewModel.onNewGame()

        hideDamage()

        setButtonColors(getColor(R.color.colorPrimary))

        show(hauntRollButton)
        show(omenCardCountTextView)
    }

    fun onRollDice(view: View) {
        hideDamage()
        viewModel.onRollDice()
    }

    private fun updateDamage(damageTextView: TextView, damage: Int) {
        if (damage == 0) {
            hide(damageTextView)
        } else {
            damageTextView.text = damage.toString()
            fadeIn(damageTextView)
        }
    }

    /**
     * Number picker setup
     */
    private fun setNumberPickerValues(numberPicker: NumberPicker, playerNumber: Int) {
        numberPicker.maxValue = 8
        numberPicker.minValue = 1
        numberPicker.setOnValueChangedListener {_, _, newDieCount -> viewModel.setDieCount(playerNumber, newDieCount)}
    }

    /**
     * Changes app style for haunt
     */
    private fun styleHaunt() {
        hide(hauntRollButton)
        hide(omenCardCountTextView)

        alertDialogTheme = R.style.MaterialAlertDialogHaunt

        setButtonColors(getColor(R.color.colorSecondary))
    }

    private fun beginHaunt() {
        hide(constraintLayout)
        fadeIn(constraintLayout)
        Toast.makeText(this, getString(R.string.haunt_begins), Toast.LENGTH_LONG).show()

        viewModel.beginHauntComplete()
    }

    /**
     * Changes button colors
     */
    private fun setButtonColors(color: Int) {
        attackButton.setTextColor(color)
        newGameButton.setTextColor(color)
        rollDiceButton.setTextColor(color)
    }

    /** Visibility functions **/

    private fun fadeIn(view: View, delay: Long = 0, length: Long = 1000) {
        show(view)
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
            duration = length
            startDelay = delay
            start()
        }
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
        hide(tieTextView)
    }
}
