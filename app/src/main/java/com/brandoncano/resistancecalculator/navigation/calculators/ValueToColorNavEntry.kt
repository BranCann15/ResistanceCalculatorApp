package com.brandoncano.resistancecalculator.navigation.calculators

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.brandoncano.resistancecalculator.constants.Symbols
import com.brandoncano.resistancecalculator.data.ESeriesCardContent
import com.brandoncano.resistancecalculator.model.ResistorViewModelFactory
import com.brandoncano.resistancecalculator.model.vtc.ResistorVtcViewModel
import com.brandoncano.resistancecalculator.navigation.Screen
import com.brandoncano.resistancecalculator.navigation.navigateToAbout
import com.brandoncano.resistancecalculator.navigation.navigateToColorToValue
import com.brandoncano.resistancecalculator.navigation.navigateToPreferredValuesIec
import com.brandoncano.resistancecalculator.ui.screens.vtc.ValueToColorScreen
import com.brandoncano.resistancecalculator.util.MultiplierFromUnits
import com.brandoncano.resistancecalculator.util.eseries.DeriveESeries
import com.brandoncano.resistancecalculator.util.eseries.DeriveESeriesString
import com.brandoncano.resistancecalculator.util.eseries.FindClosestStandardValue
import com.brandoncano.resistancecalculator.util.eseries.GenerateStandardValues
import com.brandoncano.resistancecalculator.util.eseries.ParseResistanceValue
import com.brandoncano.resistancecalculator.util.eseries.RoundStandardValue
import com.brandoncano.resistancecalculator.util.resistor.formatResistor
import kotlin.math.abs

fun NavGraphBuilder.valueToColorScreen(
    navHostController: NavHostController,
    onOpenThemeDialog: () -> Unit,
) {
    composable(
        route = Screen.ValueToColor.route,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
    ) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current
        val openMenu = remember { mutableStateOf(false) }
        val reset = remember { mutableStateOf(false) }
        var eSeriesCardContent: ESeriesCardContent by remember { mutableStateOf(ESeriesCardContent.NoContent) }
        val closestStandardValue = remember { mutableDoubleStateOf(10.0) }
        val viewModel: ResistorVtcViewModel = viewModel(factory = ResistorViewModelFactory(context))
        val resistor by viewModel.resistor.collectAsState()
        val isError by viewModel.isError.collectAsState()
        val navBarSelection by viewModel.navBarSelection.collectAsState()
        resistor.formatResistor()

        ValueToColorScreen(
            resistor = resistor,
            navBarPosition = navBarSelection,
            isError = isError,
            openMenu = openMenu,
            reset = reset,
            eSeriesCardContent = eSeriesCardContent,
            onOpenThemeDialog = onOpenThemeDialog,
            onNavigateBack = { navHostController.popBackStack() },
            onClearSelectionsTapped = {
                openMenu.value = false
                reset.value = true
                eSeriesCardContent = ESeriesCardContent.NoContent
                viewModel.clear()
                focusManager.clearFocus()
            },
            onAboutTapped = {
                openMenu.value = false
                navigateToAbout(navHostController)
            },
            onColorToValueTapped = {
                openMenu.value = false
                navigateToColorToValue(navHostController)
            },
            onValueChanged = { resistance, units, band5, band6, clearFocus ->
                reset.value = false
                eSeriesCardContent = ESeriesCardContent.NoContent
                viewModel.updateValues(resistance, units, band5, band6)
                if (clearFocus) focusManager.clearFocus()
            },
            onNavBarSelectionChanged = { selection ->
                viewModel.saveNavBarSelection(selection)
            },
            onValidateResistanceTapped = {
                if (resistor.isEmpty() || isError) return@ValueToColorScreen
                val resistanceValue = ParseResistanceValue.execute(resistor.resistance, resistor.units) ?: return@ValueToColorScreen
                val tolerance = if (navBarSelection == 0) "${Symbols.PM}20%" else resistor.band5
                if (tolerance.isEmpty()) {
                    eSeriesCardContent = ESeriesCardContent.InvalidTolerance("${navBarSelection + 3}")
                    return@ValueToColorScreen
                }
                val tolerancePercentage = tolerance.substring(1, tolerance.length - 1).toDoubleOrNull() ?: return@ValueToColorScreen

                val eSeriesList = DeriveESeries.execute(tolerancePercentage, navBarSelection + 3)
                if (eSeriesList.isNullOrEmpty()) {
                    eSeriesCardContent = ESeriesCardContent.InvalidTolerance("${navBarSelection + 3}")
                    return@ValueToColorScreen
                }

                val standardValues = GenerateStandardValues.execute(eSeriesList)
                val closestValueOhms = FindClosestStandardValue.execute(resistanceValue, standardValues)
                if (resistanceValue > 0.0) {
                    closestStandardValue.doubleValue = closestValueOhms / MultiplierFromUnits.execute(resistor.units)
                } else {
                    closestStandardValue.doubleValue = 0.1
                }

                val diff = abs(resistanceValue - closestValueOhms)
                eSeriesCardContent = if (diff == 0.0) {
                    val eSeriesName = DeriveESeriesString.execute(eSeriesList)
                    ESeriesCardContent.ValidResistance(eSeriesName)
                } else {
                    val resistance = RoundStandardValue.execute(closestStandardValue.doubleValue)
                    ESeriesCardContent.InvalidResistance("$resistance ${resistor.units}")
                }
                return@ValueToColorScreen
            },
            onUseValueTapped = {
                eSeriesCardContent = ESeriesCardContent.NoContent
                val resistance = RoundStandardValue.execute(closestStandardValue.doubleValue)
                viewModel.updateValues(resistance, resistor.units, resistor.band5, resistor.band6)
                focusManager.clearFocus()
                return@ValueToColorScreen resistance
            },
            onLearnMoreTapped = {
                navigateToPreferredValuesIec(navHostController)
            }
        )
    }
}