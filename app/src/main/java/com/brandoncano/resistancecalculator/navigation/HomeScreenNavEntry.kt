package com.brandoncano.resistancecalculator.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.brandoncano.resistancecalculator.ui.screens.home.HomeScreen

fun NavGraphBuilder.homeScreen(
    navHostController: NavHostController,
) {
    composable(
        route = Screen.Home.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        val context = LocalContext.current
        val openMenu = remember { mutableStateOf(false) }
        HomeScreen(
            openMenu = openMenu,
            onAboutTapped = {
                openMenu.value = false
                navigateToAbout(navHostController)
            },
            onColorToValueTapped = { navigateToColorToValue(navHostController) },
            onValueToColorTapped = { navigateToValueToColor(navHostController) },
            onSmdTapped = { navigateToSmd(navHostController) },
            onRateThisAppTapped = { navigateToGooglePlay(context) },
            onViewOurAppsTapped = { navigateToOurApps(navHostController) },
        )
    }
}
