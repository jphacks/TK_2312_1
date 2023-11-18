package com.atssystem.compose

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AtsSystemApp(
    startDestination: String,
    pushPackageName: String?
){
    val navController = rememberNavController()
    ATSSystemNavHost(
        navController = navController,
        startDestination = startDestination,
        pushPackageName = pushPackageName
    )
}

@Composable
fun ATSSystemNavHost(
    navController: NavHostController,
    pushPackageName: String?,
    startDestination: String
) {
   NavHost(navController = navController, startDestination = startDestination) {
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
           if(pushPackageName!=null) {
               bundle.putString("packageName", pushPackageName)
           } else {
               bundle.putString("packageName", packageName)
           }
           AppDetailScreen(
               viewModel = viewModel(factory = getViewModelFactory(bundle)),
               onBack = {navController.navigate("home")}
           )
       }

       composable("whenInstalled") {
           WhenInstalledScreen()
       }
   }
}