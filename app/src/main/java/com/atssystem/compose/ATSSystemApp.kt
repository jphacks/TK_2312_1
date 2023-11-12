package com.atssystem.compose

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.atssystem.AppDetailViewModel
import com.atssystem.AppListViewModel
import com.atssystem.ViewModelFactory

@Composable
fun AtsSystemApp(){
    val navController = rememberNavController()
    ATSSystemNavHost(
        navController = navController)
}

@Composable
fun ATSSystemNavHost(
    navController: NavHostController
) {
   NavHost(navController = navController, startDestination = "home") {
       composable("home") {
           HomeScreen(
               onAppClick = {name->
                   navController.navigate("list/$name")},
               viewModel = viewModel(factory = getViewModelFactory())
           )
       }

       composable("list/{packageName}",
           arguments = listOf(navArgument("packageName") { type = NavType.StringType })) {
           val packageName = it.arguments?.getString("packageName").toString()
           val bundle = Bundle()
           bundle.putString("packageName", packageName)
           AppDetailScreen(
               viewModel = viewModel(factory = getViewModelFactory(bundle))
           )
       }
   }
}