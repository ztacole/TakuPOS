package com.takumi.takupos.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.takumi.takupos.R
import com.takumi.takupos.ui.navigation.NavigationItem
import com.takumi.takupos.ui.navigation.Screen

@Composable
fun TakuPOSBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.shadow(0.5.dp),
        containerColor = Color.Transparent
        ) {
        val navBackstack by navController.currentBackStackEntryAsState()
        val currentRoute = navBackstack?.destination?.route
        val navigationList = listOf(
            NavigationItem(
                title = "Beranda",
                route = Screen.Home.route,
                selectedIcon = R.drawable.home_filled,
                unselectedIcon = R.drawable.home_outlined
            ),
            NavigationItem(
                title = "Scan",
                route = "QR",
                selectedIcon = R.drawable.scan_qr,
                unselectedIcon = R.drawable.scan_qr
            ),
            NavigationItem(
                title = "Riwayat",
                route = Screen.History.route,
                selectedIcon = R.drawable.history_trx_filled,
                unselectedIcon = R.drawable.history_trx_outlined
            )
        )
        navigationList.map { menu ->
            NavigationBarItem(
                selected = currentRoute == menu.route,
                onClick = {
                    navController.navigate(menu.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == menu.route) menu.selectedIcon else menu.unselectedIcon,
                        ),
                        contentDescription = menu.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(text = menu.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.secondary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}