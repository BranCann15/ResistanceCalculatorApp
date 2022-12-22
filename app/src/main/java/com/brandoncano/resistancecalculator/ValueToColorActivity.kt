package com.brandoncano.resistancecalculator

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.brandoncano.resistancecalculator.spinner.ImageTextArrayAdapter
import com.brandoncano.resistancecalculator.spinner.SpinnerContents
import com.brandoncano.resistancecalculator.util.*
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * @author: Brandon
 *
 * Job: activity for the value to color page.
 */
class ValueToColorActivity : AppCompatActivity() {

    companion object {
        private const val EMPTY_STRING = ""
    }

    private lateinit var screenText: TextView
    private lateinit var numberBand1: ImageView
    private lateinit var numberBand2: ImageView
    private lateinit var numberBand3: ImageView
    private lateinit var multiplierBand: ImageView
    private lateinit var toleranceBand: ImageView
    private lateinit var ppmBand: ImageView
    private lateinit var toggleDropDown: TextInputLayout

    private var imageSelection = 4
    private var buttonCheck = EMPTY_STRING
    private var resistance = EMPTY_STRING
    private var toleranceColor = EMPTY_STRING
    private var ppmColor = EMPTY_STRING
    private var units = EMPTY_STRING
    private var shareColors: Array<Int> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_value_to_color)

        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) {
            val colorDrawable = ColorDrawable(getColor(R.color.orange_primary))
            actionBar.setBackgroundDrawable(colorDrawable)
            actionBar.title = getString(R.string.value_to_color)
            actionBar.elevation = 4F
        }

        idSetup()
        dropDownSetup()
        buttonSetup()
        calculateButtonSetup()
        screenText.text = loadStateData(StateData.RESISTANCE_CTV)
        if (screenText.text.isEmpty()) screenText.text = getString(R.string.enter_value)
    }

    override fun onResume() {
        super.onResume()
        idSetup()
        dropDownSetup()
        buttonSetup()
        calculateButtonSetup()
        screenText.text = loadStateData(StateData.RESISTANCE_CTV)
        if (screenText.text.isEmpty()) screenText.text = getString(R.string.enter_value)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_dropdown_vtc, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.color_to_value -> {
                super.finish()
                startActivity(Intent(this, ColorToValueActivity::class.java))
                return true
            }
            R.id.show_resistor_charts -> {
                return ResistorChart.show(this, imageSelection)
            }
            R.id.share_item -> {
                val intent = ShareResistance.shareItemVTC(
                    imageSelection, shareColors, screenText, toleranceColor, ppmColor
                )
                startActivity(Intent.createChooser(intent, EMPTY_STRING))
                return true
            }
            R.id.feedback -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = EmailFeedback.execute()
                startActivity(intent)
                return true
            }
            R.id.about_item -> {
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun idSetup() {
        screenText = findViewById(R.id.display_resistance)
        numberBand1 = findViewById(R.id.r_band_1)
        numberBand2 = findViewById(R.id.r_band_2)
        numberBand3 = findViewById(R.id.r_band_3)
        multiplierBand = findViewById(R.id.r_band_4)
        toleranceBand = findViewById(R.id.r_band_5)
        ppmBand = findViewById(R.id.r_band_6)
        toggleDropDown = findViewById(R.id.dropDownSelectorPPM)
    }

    private fun buttonSetup() {
        val fourBandButton: Button = findViewById(R.id.four_band)
        val fiveBandButton: Button = findViewById(R.id.five_band)
        val sixBandButton: Button = findViewById(R.id.six_band)

        fun loadImage(button: String) {
            resistance = loadStateData(StateData.USER_INPUT_VTC)

            // crash fix - leaving this activity with invalid input would cause a crash
            if (resistance == "NotValid" || resistance.isEmpty()) return

            shareColors = ResistorFormatter.generateResistor(imageSelection, resistance, units)
            setBandColor(numberBand1, shareColors[0])
            setBandColor(numberBand2, shareColors[1])
            setBandColor(multiplierBand, shareColors[3])
            setBandColor(toleranceBand, ColorFinder.textToColor(toleranceColor))

            if (button == "5" || button == "6") setBandColor(numberBand3, shareColors[2])
            if (button == "6") setBandColor(ppmBand, ColorFinder.textToColor(ppmColor))
        }

        when (loadStateData(StateData.BUTTON_SELECTION_VTC)) {
            "4" -> {
                buttonListener(fourBandButton, fiveBandButton, sixBandButton, 4, View.INVISIBLE)
                loadImage("4")
            }
            "5" -> {
                buttonListener(fiveBandButton, fourBandButton, sixBandButton, 5, View.INVISIBLE)
                loadImage("5")
            }
            "6" -> {
                buttonListener(sixBandButton, fourBandButton, fiveBandButton, 6, View.VISIBLE)
                loadImage("6")
            }
        }

        // toggle four band resistor
        fourBandButton.setOnClickListener {
            buttonListener(fourBandButton, fiveBandButton, sixBandButton, 4, View.INVISIBLE)
        }

        // toggle five band resistor
        fiveBandButton.setOnClickListener {
            buttonListener(fiveBandButton, fourBandButton, sixBandButton, 5, View.INVISIBLE)
        }

        // toggle six band resistor
        sixBandButton.setOnClickListener {
            buttonListener(sixBandButton, fourBandButton, fiveBandButton, 6, View.VISIBLE)
        }
    }

    private fun buttonListener(
        selectedBtn: Button, btn1: Button, btn2: Button, btnNumber: Int, view: Int
    ) {
        selectedBtn.setBackgroundColor(getColor(R.color.mango_dark))
        btn1.setBackgroundColor(getColor(R.color.mango_primary))
        btn2.setBackgroundColor(getColor(R.color.mango_primary))

        toggleDropDown.visibility = view
        imageSelection = btnNumber
        if (resistance != EMPTY_STRING) resistance = errorFinder(buttonCheck)
        saveStateData(StateData.BUTTON_SELECTION_VTC, "$imageSelection")
    }

    private fun calculateButtonSetup() {
        val inputResistance: EditText = findViewById(R.id.enter_resistance)
        inputResistance.setText(loadStateData(StateData.USER_INPUT_VTC))
        inputResistance.doOnTextChanged { text, _, _, _ ->
            resistance = errorFinder(text.toString())
        }

        // make the resistor
        val calculateButton: Button = findViewById(R.id.calculate)
        calculateButton.setOnClickListener {
            val colors: Array<Int> =
                ResistorFormatter.generateResistor(imageSelection, resistance, units)
            shareColors = colors // for sharing in menu
            if (colors.isNotEmpty()) {
                setBandColor(numberBand1, colors[0])
                setBandColor(numberBand2, colors[1])
                setBandColor(numberBand3, colors[2])
                setBandColor(multiplierBand, colors[3])
                setBandColor(toleranceBand, ColorFinder.textToColor(toleranceColor))

                screenText.text = updateText(colors)
                saveStateData(StateData.RESISTANCE_VTC, screenText.text.toString())
            }
            // prevents an invalid input from being saved
            if (resistance != "NotValid") {
                saveStateData(StateData.USER_INPUT_VTC, resistance)
            }
            closeKeyboard()
        }
    }

    // finds any errors in the user input
    private fun errorFinder(text: String): String {
        val textInputLayout: TextInputLayout = findViewById(R.id.edit_text_outline)
        return if (text.isEmpty() || text == ".") {
            textInputLayout.error = null
            buttonCheck = EMPTY_STRING
            EMPTY_STRING
        } else if (!ResistorFormatter.isValidInput(imageSelection, text, units)) {
            textInputLayout.error = "Invalid Input"
            buttonCheck = text
            "NotValid"
        } else {
            textInputLayout.error = null
            buttonCheck = text
            text
        }
    }

    private fun updateText(colors: Array<Int>): String {
        return when (imageSelection) {
            4 -> {
                setBandColor(numberBand3, ColorFinder.textToColor())
                setBandColor(ppmBand, ColorFinder.textToColor())
                "$resistance $units $toleranceColor"
            }
            5 -> {
                setBandColor(numberBand3, colors[2])
                setBandColor(ppmBand, ColorFinder.textToColor())
                "$resistance $units $toleranceColor"
            }
            6 -> {
                setBandColor(numberBand3, colors[2])
                setBandColor(ppmBand, ColorFinder.textToColor(ppmColor))
                if (ppmColor.isEmpty()) "$resistance $units $toleranceColor" else "$resistance $units $toleranceColor\n$ppmColor"
            }
            else -> EMPTY_STRING
        }
    }

    private fun closeKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun dropDownSetup() {
        val dropDownUnits: AutoCompleteTextView = findViewById(R.id.spinnerUnits)
        val dropDownTolerance: AutoCompleteTextView = findViewById(R.id.spinnerTolerance)
        val dropDownPPM: AutoCompleteTextView = findViewById(R.id.spinnerPPM)

        // load and set saved data
        units = loadStateData(StateData.UNITS_DROPDOWN_VTC)
        dropDownUnits.setText(units)

        toleranceColor = loadStateData(StateData.TOLERANCE_DROPDOWN_VTC)
        dropDownTolerance.setText(toleranceColor)
        setDropDownDrawable(dropDownTolerance, toleranceColor)

        ppmColor = loadStateData(StateData.PPM_DROPDOWN_VTC)
        dropDownPPM.setText(ppmColor)
        setDropDownDrawable(dropDownPPM, ppmColor)

        // create and set adapters
        ArrayAdapter(
            this, R.layout.spinner_units_layout, SpinnerContents.unitsArray
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_units_layout)
            dropDownUnits.setAdapter(adapter)
        }

        val toleranceAdapter = ImageTextArrayAdapter(this, SpinnerContents.toleranceTextArray)
        dropDownTolerance.setAdapter(toleranceAdapter)
        val ppmAdapter = ImageTextArrayAdapter(this, SpinnerContents.ppmTextArray)
        dropDownPPM.setAdapter(ppmAdapter)

        // listeners
        dropDownUnits.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                units = dropDownUnits.adapter.getItem(position).toString()
                if (resistance != EMPTY_STRING) resistance = errorFinder(buttonCheck)
                saveStateData(StateData.UNITS_DROPDOWN_VTC, dropDownUnits.text.toString())
            }

        dropDownTolerance.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                toleranceColor = dropDownTolerance.adapter.getItem(position).toString()
                setDropDownDrawable(dropDownTolerance, toleranceColor)
                saveStateData(StateData.TOLERANCE_DROPDOWN_VTC, dropDownTolerance.text.toString())
            }

        dropDownPPM.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                ppmColor = dropDownPPM.adapter.getItem(position).toString()
                setDropDownDrawable(dropDownPPM, ppmColor)
                saveStateData(StateData.PPM_DROPDOWN_VTC, dropDownPPM.text.toString())
            }
    }

    private fun setBandColor(band: ImageView, color: Int) {
        band.setColorFilter(ContextCompat.getColor(this, color))
    }

    private fun setDropDownDrawable(dropDown: AutoCompleteTextView, color: String) {
        dropDown.setCompoundDrawablesRelativeWithIntrinsicBounds(
            ColorFinder.textToColoredDrawable(color), 0, 0, 0
        )
    }

    // saves the user input
    private fun saveStateData(data: StateData, input: String) {
        val sharedPreferences = getSharedPreferences(data.name_, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json: String = gson.toJson(input)
        editor.putString(data.key_, json)
        editor.apply()
    }

    // loads the user input
    private fun loadStateData(data: StateData): String {
        val sharedPreferences = getSharedPreferences(data.name_, MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(data.key_, null)
        val type: Type = object : TypeToken<String?>() {}.type
        return gson.fromJson<String?>(json, type) ?: return EMPTY_STRING
    }
}
