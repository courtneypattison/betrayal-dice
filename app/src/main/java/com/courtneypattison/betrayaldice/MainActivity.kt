package com.courtneypattison.betrayaldice

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
            if (unsuccessfulHauntRoll()) showNoHaunt()
        })

        viewModel.player0Damage.observe(this, Observer<Int> { damage ->
            updateDamage(player0DamageTextView, damage)
        })

        viewModel.player1Damage.observe(this, Observer<Int> { damage ->
            updateDamage(player1DamageTextView, damage)
        })

        viewModel.player0DieCount.observe(this, Observer<Int> { dieCount ->
            removeOutcomeProbabilities()
            addOutcomeProbabilities(dieCount)
        })

        viewModel.player0Score.observe(this, Observer<Int> { score ->
            player0ScoreTextView.text = score.toString()
            fadeIn(player0ScoreTextView)
        })

        viewModel.player0ScorePrev.observe(this, Observer<Int> { scorePrev ->
            player0ScorePrevTextView.text = scorePrev.toString()
            fadeIn(player0ScorePrevTextView)
        })

        viewModel.player1Score.observe(this, Observer<Int> { score ->
            player1ScoreTextView.text = score.toString()
            fadeIn(player1ScoreTextView)
        })

        viewModel.player1ScorePrev.observe(this, Observer<Int> { scorePrev ->
            player1ScorePrevTextView.text = scorePrev.toString()
            fadeIn(player1ScorePrevTextView)
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
            .setTitle(getString(R.string.new_game))
            .setMessage(getString(R.string.new_game_message))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.new_game)) { _, _ -> onNewGameConfirm() }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .create()
            .show()
    }

    private fun onNewGameConfirm() {
        viewModel.onNewGame()

        hideDamage()

        alertDialogTheme = R.style.MaterialAlertDialogCustom
        setAllButtonColors(getColor(R.color.colorPrimary), R.color.button_primary)

        show(hauntRollButton)
        show(omenCardCountTextView)
    }

    fun onRollDice(view: View) {
        hideDamage()
        viewModel.onRollDice()
    }

    /**
     * Checks if haunt roll started the haunt
     */
    private fun unsuccessfulHauntRoll(): Boolean {
        return !viewModel.isHaunt.value!! && viewModel.omenCardCount.value!! > 0
    }

    /**
     * Update damage text view's visibility and text
     */
    private fun updateDamage(damageTextView: TextView, damage: Int) {
        if (damage == 0) {
            hide(damageTextView)
        } else {
            damageTextView.text = damage.toString()
            fadeIn(damageTextView)
        }
    }

    /**
     * Add outcome probabilities to table
     */
    private fun addOutcomeProbabilities(dieCount: Int) {
        for (i in 0..16) {
            outcomeTableLayout.setColumnStretchable(i, true)

            val outcomeTextView = TextView(this)
            outcomeTextView.text = i.toString()
            outcomeTableRow.addView(outcomeTextView)
        }

        for (outcomeProbability in viewModel.outcomeProbabilities[dieCount]) {
            val outcomeProbabilityTextView = TextView(this)
            outcomeProbabilityTextView.text = outcomeProbability.toString()
            outcomeProbabilityTextView.setPadding(0, 0, 32, 0)
            outcomeProbabilitiesTableRow.addView(outcomeProbabilityTextView)
        }
    }

    /**
     * Remove outcome probabilities from table
     */
    private fun removeOutcomeProbabilities() {
        outcomeTableRow.removeAllViews()
        outcomeProbabilitiesTableRow.removeAllViews()
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

        setAllButtonColors(getColor(R.color.colorSecondary), R.color.button_secondary)
    }

    /**
     * Changes app when haunt first begins (happens only once per game)
     */
    private fun beginHaunt() {
        hide(constraintLayout)
        fadeIn(constraintLayout)
        showHauntBegins()

        viewModel.beginHauntComplete()
    }

    /**
     * Changes button color, including ripple color
     */
    private fun setButtonColor(button: Button, color: Int, colorStateListID: Int) {
        button.setTextColor(color)
        (button as MaterialButton).rippleColor = getColorStateList(colorStateListID)
    }

    /**
     * Changes all visible button colors
     */
    private fun setAllButtonColors(color: Int, colorStateListID: Int) {
        setButtonColor(attackButton, color, colorStateListID)
        setButtonColor(newGameButton, color, colorStateListID)
        setButtonColor(rollDiceButton, color, colorStateListID)
    }

    /**
     * Shows alert dialog with haunt begins message
     */
    private fun showHauntBegins() {
        MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogHaunt)
            .setTitle(getString(R.string.haunt_begins))
            .setPositiveButton(getString(R.string.continue_please)) { _, _ -> }
            .create()
            .show()
    }

    /**
     * Shows snackbar with no haunt message
     */
    private fun showNoHaunt() {
        Snackbar.make(omenCardCountTextView, getString(R.string.no_haunt), Snackbar.LENGTH_SHORT).show()
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
