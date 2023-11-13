package io.github.trueangle.cocktail.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.trueangle.cocktail.ui.AppViewModelFactory
import io.github.trueangle.cocktail.ui.categories.CategoriesScreen

enum class HomeRoutes(val route: String) {
    Categories("home/categories"),
    Favorites("home/favorites"),
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(modifier: Modifier, appViewModelFactory: AppViewModelFactory) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = { HomeBottomBar() }
    ) { _ ->
        NavHost(
            modifier = Modifier,
            navController = navController,
            startDestination = HomeRoutes.Categories.route
        ) {

            composable(route = HomeRoutes.Categories.route) {
                CategoriesScreen(
                    modifier = Modifier.fillMaxSize(),
                    store = viewModel(factory = appViewModelFactory)
                )
            }

            composable(route = HomeRoutes.Favorites.route) {

            }
        }
    }
}

@Composable
private fun HomeBottomBar() {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.List, contentDescription = null) },
            selected = true,
            onClick = {
                /* if (route != HomeRoutes.Featured.routePattern) {
                     coordinator.onBottomNavActionClick(HomeRoutes.Featured)
                 }*/
            },
            label = { Text(text = "Categories") },
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.Star, contentDescription = null) },
            selected = false,
            onClick = {
                /* if (route != HomeRoutes.Trending.routePattern) {
                     coordinator.onBottomNavActionClick(HomeRoutes.Trending)
                 }*/
            },
            label = { Text(text = "Favorites") }
        )
    }
}

@Preview
@Composable
private fun HomePreview() {

}