package com.brandoncano.resistancecalculator.ui.screens.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.brandoncano.resistancecalculator.R
import com.brandoncano.resistancecalculator.constants.Colors as C
import com.brandoncano.resistancecalculator.model.ctv.ResistorCtv
import com.brandoncano.resistancecalculator.ui.screens.ctv.ResistorLayout
import com.brandoncano.resistancecalculator.ui.theme.ResistorCalculatorTheme
import com.brandoncano.sharedcomponents.composables.AppScreenPreviews
import com.brandoncano.sharedcomponents.composables.AppTopAppBar
import com.brandoncano.sharedcomponents.text.onSurfaceVariant
import com.brandoncano.sharedcomponents.text.textStyleBody
import com.brandoncano.sharedcomponents.text.textStyleLargeTitle
import com.brandoncano.sharedcomponents.text.textStyleTitle

/**
 * Note: Information originated from - https://eepower.com/resistor-guide/resistor-standards-and-codes/resistor-color-code/#
 */

@Composable
fun LearnColorCodesScreen(
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                titleText = stringResource(R.string.info_title),
                navigationIcon =  Icons.Filled.Close,
                onNavigateBack = onNavigateBack,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        LearnColorCodesScreenContent(paddingValues)
    }
}

@Composable
private fun LearnColorCodesScreenContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.info_color_intro_title),
            modifier = Modifier.padding(vertical = 12.dp),
            style = textStyleLargeTitle(),
        )
        Text(
            text = stringResource(R.string.info_color_intro_body),
            modifier = Modifier.padding(bottom = 32.dp),
            style = textStyleBody().onSurfaceVariant(),
        )

        Text(
            text = stringResource(R.string.info_color_meaning_title),
            modifier = Modifier.padding(bottom = 12.dp),
            style = textStyleLargeTitle(),
        )
        Text(
            text = stringResource(R.string.info_color_meaning_body),
            modifier = Modifier.padding(bottom = 12.dp),
            style = textStyleBody().onSurfaceVariant(),
        )
        ResistorColorCodeTable()
        Spacer(modifier = Modifier.height(24.dp))

        BandSection(
            headlineRes = R.string.info_color_three_band_headline,
            bodyRes = R.string.info_color_three_band_body,
            codeRes = R.string.info_color_three_band_code,
            descriptionRes = R.string.info_color_three_band_description,
            resistor = ResistorCtv(C.RED, C.VIOLET, "", C.ORANGE, "", "", 0),
        )
        Spacer(modifier = Modifier.height(24.dp))
        BandSection(
            headlineRes = R.string.info_color_four_band_headline,
            bodyRes = R.string.info_color_four_band_body,
            codeRes = R.string.info_color_four_band_code,
            descriptionRes = R.string.info_color_four_band_description,
            resistor = ResistorCtv(C.YELLOW, C.VIOLET, "", C.RED, C.GOLD, "", 1),
        )
        Spacer(modifier = Modifier.height(24.dp))
        BandSection(
            headlineRes = R.string.info_color_five_band_headline,
            bodyRes = R.string.info_color_five_band_body,
            codeRes = R.string.info_color_five_band_code,
            descriptionRes = R.string.info_color_five_band_description,
            resistor = ResistorCtv(C.BROWN, C.GREEN, C.BLACK, C.ORANGE, C.BROWN, "", 2),
        )
        Spacer(modifier = Modifier.height(24.dp))
        BandSection(
            headlineRes = R.string.info_color_six_band_headline,
            bodyRes = R.string.info_color_six_band_body,
            codeRes = R.string.info_color_six_band_code,
            descriptionRes = R.string.info_color_six_band_description,
            resistor = ResistorCtv(C.GREEN, C.BLUE, C.BLACK, C.BROWN, C.RED, C.BROWN, 3),
        )
        DisclaimerText()
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun BandSection(
    headlineRes: Int,
    bodyRes: Int,
    codeRes: Int,
    descriptionRes: Int,
    resistor: ResistorCtv,
) {
    Text(
        text = stringResource(headlineRes),
        modifier = Modifier.padding(bottom = 12.dp),
        style = textStyleTitle(),
    )
    Text(
        text = stringResource(bodyRes),
        style = textStyleBody().onSurfaceVariant(),
        modifier = Modifier.padding(bottom = 12.dp),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ResistorLayout(resistor)
    }
    Spacer(modifier = Modifier.height(12.dp))
    CodeExampleCard(
        code = stringResource(codeRes),
        description = stringResource(descriptionRes),
    )
}

@AppScreenPreviews
@Composable
private fun LearnColorCodesScreenPreview() {
    ResistorCalculatorTheme {
        LearnColorCodesScreen {  }
    }
}
