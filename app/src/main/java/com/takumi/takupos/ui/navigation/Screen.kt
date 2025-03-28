package com.takumi.takupos.ui.navigation

sealed class Screen(val route: String){
    data object Home: Screen("home")
    data object History: Screen("history")
    data object Scan: Screen("Scan")
}