package io.github.trueangle.cocktail.ui.home

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

sealed class HomeRoutes {
    data object CategoriesNavigation : HomeRoutes() {
        const val RoutePattern: String = "home/categories"
    }

    data object Categories : HomeRoutes() {
        const val RoutePattern: String = "home/categories/screen"
    }

    data object Drinks : HomeRoutes() {
        const val RoutePattern: String = "home/categories/drinks/{categoryName}"

        fun routeWithParam(categoryName: String) = "home/categories/drinks/${categoryName.encodeUrl()}"
    }

    data object DrinkDetail : HomeRoutes() {
        const val RoutePattern: String = "home/drink/{id}"

        fun routeWithParam(drinkId: String) = "home/drink/${drinkId}"
    }

    data object Favorites : HomeRoutes() {
        const val RoutePattern: String = "home/favorites"
    }
}

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
            startDestination = HomeRoutes.CategoriesNavigation.RoutePattern
        ) {

            navigation(
                startDestination = HomeRoutes.Categories.RoutePattern,
                route = HomeRoutes.CategoriesNavigation.RoutePattern
            ) {
                composable(route = HomeRoutes.Categories.RoutePattern) {
                    CategoriesScreen(
                        modifier = Modifier.fillMaxSize(),
                        vm = viewModel(factory = appViewModelFactory),
                        onItemClick = { cat ->
                            navController.navigate(HomeRoutes.Drinks.routeWithParam(cat.name))
                        },
                        onSearchItemClick = { drink ->
                            navController.navigate(HomeRoutes.DrinkDetail.routeWithParam(drink.id))
                        }
                    )
                }

                composable(
                    route = HomeRoutes.DrinkDetail.RoutePattern,
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

            composable(route = HomeRoutes.Favorites.RoutePattern) {
                FavoritesScreen(
                    modifier = Modifier.fillMaxSize(),
                    vm = viewModel(factory = appViewModelFactory),
                ) { drink ->
                    navController.navigate(HomeRoutes.DrinkDetail.routeWithParam(drink.id))
                }
            }

            composable(
                route = HomeRoutes.Drinks.RoutePattern,
                arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
            ) {
                DrinksScreen(
                    modifier = Modifier.fillMaxSize(),
                    vm = viewModel(factory = appViewModelFactory),
                    onItemClick = { drink ->
                        navController.navigate(HomeRoutes.DrinkDetail.routeWithParam(drink.id))
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
            selected = destination?.hierarchy?.any { it.route?.contains("home/categories") == true } == true,
            onClick = {
                navController.navigate(HomeRoutes.CategoriesNavigation.RoutePattern) {
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
            selected = destination?.hierarchy?.any { it.route == HomeRoutes.Favorites.RoutePattern } == true,
            onClick = {
                navController.navigate(HomeRoutes.Favorites.RoutePattern) {
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