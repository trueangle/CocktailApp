package io.github.trueangle.cocktail.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import io.github.trueangle.cocktail.ui.AppViewModelFactory
import io.github.trueangle.cocktail.ui.categories.CategoriesScreen
import io.github.trueangle.cocktail.ui.drinkdetail.DrinkDetailScreen
import io.github.trueangle.cocktail.ui.drinks.DrinksScreen
import io.github.trueangle.cocktail.ui.favorites.FavoritesScreen
import io.github.trueangle.cocktail.util.encodeUrl

enum class HomeRoutes(val route: String) {
    CategoriesNavigation("home/categories"),
    Categories("home/categories/screen"),
    Drinks("home/drinks/{categoryName}"),
    DrinkDetail("home/drink/{id}"),
    Favorites("home/favorites"),
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(modifier: Modifier, appViewModelFactory: AppViewModelFactory) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = { HomeBottomBar(navController) }
    ) {

        NavHost(
            modifier = Modifier.padding(bottom = it.calculateBottomPadding()),
            navController = navController,
            startDestination = HomeRoutes.CategoriesNavigation.route
        ) {

            navigation(
                startDestination = HomeRoutes.Categories.route,
                route = HomeRoutes.CategoriesNavigation.route
            ) {
                composable(route = HomeRoutes.Categories.route) {
                    CategoriesScreen(
                        modifier = Modifier.fillMaxSize(),
                        vm = viewModel(factory = appViewModelFactory)
                    ) { cat ->
                        // todo
                        navController.navigate("home/drinks/${cat.name.encodeUrl()}")
                    }
                }

                composable(
                    route = HomeRoutes.DrinkDetail.route,
                    arguments = listOf(navArgument("id") { type = NavType.StringType })
                ) {
                    DrinkDetailScreen(
                        modifier = Modifier.fillMaxSize(),
                        vm = viewModel(factory = appViewModelFactory),
                    ) {
                        navController.popBackStack()
                    }
                }
            }

            composable(route = HomeRoutes.Favorites.route) {
                FavoritesScreen(
                    modifier = Modifier.fillMaxSize(),
                    vm = viewModel(factory = appViewModelFactory),
                ) { drink ->
                    navController.navigate("home/drink/${drink.id}")
                }
            }

            composable(
                route = HomeRoutes.Drinks.route,
                arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
            ) {
                DrinksScreen(
                    modifier = Modifier.fillMaxSize(),
                    vm = viewModel(factory = appViewModelFactory),
                    onItemClick = { drink ->
                        // todo
                        navController.navigate("home/drink/${drink.id}")
                    },
                    onNavUp = navController::popBackStack
                )
            }
        }
    }
}

@Composable
private fun HomeBottomBar(navController: NavController) {

    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.List, contentDescription = null) },
            selected = destination?.route?.contains("home/categories") == true,
            onClick = {
                navController.navigate(HomeRoutes.CategoriesNavigation.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text(text = "Categories") },
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Outlined.Star, contentDescription = null) },
            selected = destination?.hierarchy?.any { it.route == HomeRoutes.Favorites.route } == true,
            onClick = {
                navController.navigate(HomeRoutes.Favorites.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text(text = "Favorites") }
        )
    }
}

@Preview
@Composable
private fun HomePreview() {

}