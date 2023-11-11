package com.atssystem.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.atssystem.HomeViewModel

@Composable
fun AtsSystemApp(
    homeVM: HomeViewModel
){
    val navController = rememberNavController()
    ATSSystemNavHost(
        navController = navController,
        homeVM = homeVM,)
}

@Composable
fun ATSSystemNavHost(
    navController: NavHostController,
    homeVM: HomeViewModel
) {
   NavHost(navController = navController, startDestination = "home") {
       composable("home") {
           HomeScreen(
               onAppClick = {
               navController.navigate("list/$it")},
               viewModel = homeVM,
           )
       }

       composable("list/{appId}"){
           val appId = it.arguments?.getString("appId")

       }
   }
}