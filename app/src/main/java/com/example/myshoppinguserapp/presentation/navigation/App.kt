package com.example.myshoppinguserapp.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.myshoppinguserapp.presentation.screen.home.AllCategoriesScreenUI
import com.example.myshoppinguserapp.presentation.screen.product.CategoryDetailsScreenUI
import com.example.myshoppinguserapp.presentation.screen.home.HomeScreenUI
import com.example.myshoppinguserapp.presentation.screen.profile.LoginInScreenUI
import com.example.myshoppinguserapp.presentation.screen.product.ProductDetailScreenUI
import com.example.myshoppinguserapp.presentation.screen.profile.SignUpScreenUI
import com.example.myshoppinguserapp.presentation.screen.cart.CartScreenUI
import com.example.myshoppinguserapp.presentation.screen.cart.CheckoutScreenUI
import com.example.myshoppinguserapp.presentation.screen.cart.OrdersScreenUI
import com.example.myshoppinguserapp.presentation.screen.home.AllProductsScreenUI
import com.example.myshoppinguserapp.presentation.screen.profile.ProfileScreenUI
import com.example.myshoppinguserapp.presentation.screen.wishlist.WishListScreenUI
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(auth: FirebaseAuth) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Bottom bar visibility
    val showBottomBar = currentRoute in listOf(
        Routes.HomeScreen::class.qualifiedName,
        Routes.WishListScreen::class.qualifiedName,
        Routes.CartScreen::class.qualifiedName,
        Routes.ProfileScreen::class.qualifiedName
    )

    // Determine start destination based on login state
    val startDestination = if (auth.currentUser != null) {
        SubNavigation.MainScreen
    } else {
        SubNavigation.LoginSignUpScreen
    }

    val bottomNavItems = listOf(
        BottomNavItem(
            route = Routes.HomeScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = Routes.WishListScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder
        ),
        BottomNavItem(
            route = Routes.CartScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.ShoppingCart,
            unselectedIcon = Icons.Outlined.ShoppingCart
        ),
        BottomNavItem(
            route = Routes.ProfileScreen::class.qualifiedName!!,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.primary) {
                    bottomNavItems.forEach { item ->
                        val isSelected = item.route == currentRoute
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    navController.navigate(item.route)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            NavHost(navController = navController, startDestination = startDestination) {
                navigation<SubNavigation.LoginSignUpScreen>(startDestination = Routes.LoginScreen) {
                    composable<Routes.LoginScreen> {
                        LoginInScreenUI(navController = navController)
                    }
                    composable<Routes.SignUpScreen> {
                        SignUpScreenUI(navController = navController)
                    }
                }

                navigation<SubNavigation.MainScreen>(startDestination = Routes.HomeScreen) {
                    composable<Routes.HomeScreen> {
                        HomeScreenUI(navController = navController)
                    }
                    composable<Routes.ProductDetailsScreen> {
                        val data = it.toRoute<Routes.ProductDetailsScreen>()
                        ProductDetailScreenUI(productId = data.productId)
                    }
                    composable<Routes.ProfileScreen> {
                        ProfileScreenUI(navController = navController)
                    }
                    composable<Routes.CartScreen> {
                        CartScreenUI(navController = navController)
                    }
                    composable<Routes.WishListScreen> {
                        WishListScreenUI(navController = navController)
                    }
                    composable<Routes.CategoriesScreen> {
                        AllCategoriesScreenUI(navController = navController)
                    }
                    composable<Routes.CategoryDetailsScreen> {
                        val data = it.toRoute<Routes.CategoryDetailsScreen>()
                        CategoryDetailsScreenUI(navController = navController, category = data.category)
                    }
                    composable<Routes.AllProductScreen> {
                        AllProductsScreenUI(navController = navController)
                    }

                    composable<Routes.OrderScreen> {
                        OrdersScreenUI()
                    }
                }

                composable<Routes.CheckoutScreen> {
                    val data = it.toRoute<Routes.CheckoutScreen>()
                    CheckoutScreenUI(
                        totalPrice = data.totalPrice,
                        navController = navController
                    )
                }

            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)