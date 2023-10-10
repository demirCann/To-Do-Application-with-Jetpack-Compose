package com.demircandemir.todoapp.navigation


import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.demircandemir.todoapp.ui.screens.list.ListScreen
import com.demircandemir.todoapp.ui.viewmodels.SharedViewModel
import com.demircandemir.todoapp.util.Constants.LIST_ARGUMENT_KEY
import com.demircandemir.todoapp.util.Constants.LIST_SCREEN
import com.demircandemir.todoapp.util.toAction

@ExperimentalMaterialApi
fun NavGraphBuilder.listcomposable(
    navigateToTaskScreen : (taskId : Int) -> Unit,
    sharedViewModel: SharedViewModel
){
    composable(
        route = LIST_SCREEN,
        arguments = listOf(navArgument(LIST_ARGUMENT_KEY){
            type = NavType.StringType
        })
    ) { navBackStackEntry ->
        val action = navBackStackEntry.arguments?.getString(LIST_ARGUMENT_KEY).toAction()

        LaunchedEffect(key1 = action) {
            sharedViewModel.action.value = action
        }


        ListScreen(
            navigateToTaskScreen = navigateToTaskScreen,
            sharedViewModel = sharedViewModel
        )
    }
}