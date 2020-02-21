package com.courtneypattison.betrayaldice

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setNumberPickerValues(player0NumberPicker, 0)
        this.setNumberPickerValues(player1NumberPicker, 1)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.eventHaunt.observe(this, Observer<Boolean> { isHaunt ->
            if (isHaunt) beginHaunt()
        })

        viewModel.eventTie.observe(this, Observer<Boolean> { isTie ->
            if (isTie) {
                hideDamage()
                fadeIn(tieTextView)
            } else {
                hide(tieTextView)
            }
        })

        viewModel.omenCardCount.observe(this, Observer<Int> { omenCardCount ->
            omenCardCountTextView.text = omenCardCount.toString()
        })

        viewModel.player0Damage.observe(this, Observer<Int> { damage ->
            if (damage == 0) {
                hide(player0DamageTextView)
            } else {
                player0DamageTextView.text = damage.toString()
                fadeIn(player0DamageTextView)
            }
        })

        viewModel.player1Damage.observe(this, Observer<Int> { damage ->
            if (damage == 0) {
                hide(player1DamageTextView)
            } else {
                player1DamageTextView.text = damage.toString()
                fadeIn(player1DamageTextView)
            }
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
        viewModel.onAttack()
    }

    fun onHauntRoll(view: View) {
        viewModel.onHauntRoll()
    }

    fun onNewGame(view: View) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered)
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
    private fun beginHaunt() {
        hide(hauntRollButton)
        hide(omenCardCountTextView)

        fadeOut(constraintLayout)
        Handler().postDelayed({
            setButtonColors(getColor(R.color.colorPrimaryHaunt))
            show(hauntBeginsTextView)
        }, 2000)

        fadeIn(constraintLayout, 2000)
        fadeOut(hauntBeginsTextView, 6000)

        viewModel.beginHauntComplete()
    }

    /** Color functions **/

    private fun setTextColor(button: Button, color: Int) {
        button.setTextColor(color)
    }

    private fun setButtonColors(color: Int) {
        setTextColor(rollDiceButton, color)
        setTextColor(attackButton, color)
        setTextColor(newGameButton, color)
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

    private fun fadeOut(view: View, delay: Long = 0, length: Long = 1000) {
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
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
