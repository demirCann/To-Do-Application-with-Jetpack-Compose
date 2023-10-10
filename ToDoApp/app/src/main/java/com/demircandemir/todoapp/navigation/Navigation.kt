package com.demircandemir.todoapp.navigation

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.demircandemir.todoapp.ui.viewmodels.SharedViewModel
import com.demircandemir.todoapp.util.Constants.LIST_SCREEN

@ExperimentalMaterialApi
@Composable
fun SetupNavigation(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
    val screen = remember(navController) {
        Screens(navController = navController)
    }

    NavHost(
        navController = navController,
        startDestination = LIST_SCREEN
    ){
        listcomposable(navigateToTaskScreen = screen.task, sharedViewModel = sharedViewModel)
        taskComposable(sharedViewModel = sharedViewModel, navigateToListScreen = screen.list)
    }
}