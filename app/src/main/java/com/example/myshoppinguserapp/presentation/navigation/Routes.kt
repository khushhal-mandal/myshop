package com.example.myshoppinguserapp.presentation.navigation

import kotlinx.serialization.Serializable

sealed class SubNavigation {
    @Serializable
    object LoginSignUpScreen : SubNavigation()

    @Serializable
    object MainScreen : SubNavigation()
}

sealed class Routes {
    @Serializable
    object LoginScreen : Routes()

    @Serializable
    object SignUpScreen : Routes()

    @Serializable
    object HomeScreen : Routes()

    @Serializable
    object ProfileScreen : Routes()

    @Serializable
    object CartScreen : Routes()

    @Serializable
    object WishListScreen : Routes()

    @Serializable
    data class CheckoutScreen(val totalPrice: Int) : Routes()

    @Serializable
    data class ProductDetailsScreen(
        val productId: String
    ) : Routes()

    @Serializable
    object CategoriesScreen : Routes()

    @Serializable
    object AllProductScreen : Routes()

    @Serializable
    object OrderScreen : Routes()


    @Serializable
    data class CategoryDetailsScreen(
        val category: String
    ) : Routes()
}