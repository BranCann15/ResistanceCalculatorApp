package com.brandoncano.resistancecalculator.resistor

import android.content.Context
import com.brandoncano.resistancecalculator.R
import com.brandoncano.resistancecalculator.components.StateData
import com.brandoncano.resistancecalculator.constants.Symbols
import com.brandoncano.resistancecalculator.util.ColorFinder

class ResistorVtC(val context: Context) : Resistor() {
    var userInput = ""
    var units = ""
    var toleranceValue = ""
    var ppmValue = ""

    override fun loadData() {
        userInput = StateData.USER_INPUT_VTC.loadData(context)
        units = StateData.UNITS_DROPDOWN_VTC.loadData(context)
        toleranceValue = StateData.TOLERANCE_DROPDOWN_VTC.loadData(context)
        ppmValue = StateData.PPM_DROPDOWN_VTC.loadData(context)
        resistance = StateData.RESISTANCE_VTC.loadData(context)
        if (resistance.isEmpty()) {
            resistance = context.getString(R.string.enter_value)
        }
    }

    override fun loadNumberOfBands(): String {
        return StateData.BUTTON_SELECTION_VTC.loadData(context)
    }

    override fun clear() {
        StateData.USER_INPUT_VTC.clearData(context)
        StateData.UNITS_DROPDOWN_VTC.clearData(context)
        StateData.TOLERANCE_DROPDOWN_VTC.clearData(context)
        StateData.PPM_DROPDOWN_VTC.clearData(context)
        StateData.RESISTANCE_VTC.clearData(context)
        loadData() // after clearing we want to reload the blank data
    }

    override fun updateResistance(resistance: String) {
        this.resistance = resistance
        StateData.RESISTANCE_VTC.saveData(context, resistance)
    }

    override fun updateNumberOfBands(number: Int) {
        numberOfBands = number.coerceIn(3..6)
        StateData.BUTTON_SELECTION_VTC.saveData(context, "$numberOfBands")
    }

    override fun toString(): String {
        val toleranceBand = ColorFinder.idToColorText(ColorFinder.textToColoredDrawable(toleranceValue))
        val ppmBand = ColorFinder.idToColorText(ColorFinder.textToColoredDrawable(ppmValue))
        return when (numberOfBands) {
            4 -> "[ $sigFigBandOne, $sigFigBandTwo, $multiplierBand, $toleranceBand ]"
            5 -> "[ $sigFigBandOne, $sigFigBandTwo, $sigFigBandThree, $multiplierBand, $toleranceBand ]"
            6 -> "[ $sigFigBandOne, $sigFigBandTwo, $sigFigBandThree, $multiplierBand, $toleranceBand, $ppmBand ]"
            else -> "[ $sigFigBandOne, $sigFigBandTwo, $multiplierBand ]"
        }
    }

    fun updateUserInput(input: String) {
        StateData.USER_INPUT_VTC.saveData(context, input)
    }

    fun getResistanceText(): String {
        var text = "$userInput $units "
        text += if (numberOfBands == 3) "${Symbols.PM}20%" else toleranceValue
        if (numberOfBands == 6) text += "\n$ppmValue".trimEnd('\n')
        return text
    }
}