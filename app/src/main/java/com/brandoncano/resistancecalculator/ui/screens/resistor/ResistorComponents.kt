package com.brandoncano.resistancecalculator.ui.screens.resistor

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brandoncano.resistancecalculator.R
import com.brandoncano.resistancecalculator.constants.Colors
import com.brandoncano.resistancecalculator.model.ctv.ResistorCtv
import com.brandoncano.resistancecalculator.model.vtc.ResistorVtc
import com.brandoncano.resistancecalculator.ui.composables.AppCard
import com.brandoncano.resistancecalculator.ui.composables.AppComponentPreviews
import com.brandoncano.resistancecalculator.ui.theme.ResistorCalculatorTheme
import com.brandoncano.resistancecalculator.ui.theme.textStyleTitle
import com.brandoncano.resistancecalculator.util.ColorFinder
import com.brandoncano.resistancecalculator.util.bandFiveForDisplay
import com.brandoncano.resistancecalculator.util.bandFourForDisplay
import com.brandoncano.resistancecalculator.util.bandOneForDisplay
import com.brandoncano.resistancecalculator.util.bandSixForDisplay
import com.brandoncano.resistancecalculator.util.bandThreeForDisplay
import com.brandoncano.resistancecalculator.util.bandTwoForDisplay
import com.brandoncano.resistancecalculator.util.deriveResistorColor
import com.brandoncano.resistancecalculator.util.formatResistance

private data class ResistorImagePair(@DrawableRes val drawableRes: Int, val color: String)

@Composable
fun ResistorLayout(resistor: ResistorCtv) {
    Column(
        modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val resistorColor = resistor.deriveResistorColor()
        ResistorRow(
            ResistorImagePair(R.drawable.img_resistor_wire, Colors.RESISTOR_WIRE),
            ResistorImagePair(R.drawable.img_resistor_end_left, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_96, resistor.bandOneForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_curve_left, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistor.bandTwoForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistor.bandThreeForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistor.bandFourForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_band_64_wide, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64_wide, resistor.bandFiveForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_curve_right, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_96, resistor.bandSixForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_end_right, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_wire, Colors.RESISTOR_WIRE),
        )
        ResistanceText(
            if (resistor.isEmpty()) {
                stringResource(id = R.string.default_ctv_value)
            } else {
                resistor.formatResistance()
            }
        )
    }
}

@Composable
fun ResistorLayout(
    resistor: ResistorVtc,
    isError: Boolean = false,
) {
    Column(
        modifier = Modifier.padding(top = 16.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val resistorColor = resistor.deriveResistorColor()
        ResistorRow(
            ResistorImagePair(R.drawable.img_resistor_wire, Colors.RESISTOR_WIRE),
            ResistorImagePair(R.drawable.img_resistor_end_left, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_96, resistor.bandOneForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_curve_left, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistor.bandTwoForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistor.bandThreeForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64, resistor.bandFourForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_band_64_wide, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_64_wide, resistor.bandFiveForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_curve_right, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_band_96, resistor.bandSixForDisplay()),
            ResistorImagePair(R.drawable.img_resistor_end_right, resistorColor),
            ResistorImagePair(R.drawable.img_resistor_wire, Colors.RESISTOR_WIRE),
        )
        val text = when {
            resistor.isEmpty() -> stringResource(id = R.string.default_vtc_value)
            isError -> stringResource(id = R.string.error_na)
            else -> resistor.getResistorValue()
        }
        ResistanceText(text)
    }
}

@Composable
private fun ResistorRow(vararg resistorImages: ResistorImagePair) {
    Row(horizontalArrangement = Arrangement.Absolute.Center) {
        resistorImages.forEach { resistorImage ->
            val color = ColorFinder.textToColor(resistorImage.color)
            ResistorImage(resistorImage.drawableRes, color)
        }
    }
}

@Composable
private fun ResistorImage(@DrawableRes drawableRes: Int, color: Color) {
    Image(
        painter = painterResource(id = drawableRes),
        contentDescription = null,
        colorFilter = ColorFilter.tint(color),
    )
}

@Composable
private fun ResistanceText(resistance: String) {
    AppCard(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            text = resistance,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 12.dp),
            style = textStyleTitle(),
        )
    }
}

@AppComponentPreviews
@Composable
private fun ResistorLayoutsPreview() {
    ResistorCalculatorTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ResistorLayout(ResistorCtv())
            ResistorLayout(ResistorCtv(navBarSelection = 2))
            ResistorLayout(ResistorCtv("Red", "Orange", "", "Yellow", "", "", 0))
            ResistorLayout(ResistorCtv("Red", "Orange", "", "Yellow", "Green", "", 1))
            ResistorLayout(ResistorCtv("Red", "Orange", "Black", "Yellow", "Green", "", 2))
            ResistorLayout(ResistorCtv("Red", "Orange", "Black", "Yellow", "Green", "Blue", 3))
        }
    }
}
