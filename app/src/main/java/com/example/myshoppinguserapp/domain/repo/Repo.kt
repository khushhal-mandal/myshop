package com.example.myshoppinguserapp.domain.repo

import android.net.Uri
import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.Banner
import com.example.myshoppinguserapp.domain.model.Cart
import com.example.myshoppinguserapp.domain.model.Category
import com.example.myshoppinguserapp.domain.model.Order
import com.example.myshoppinguserapp.domain.model.Product
import com.example.myshoppinguserapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getAllCategories(): Flow<ResultState<List<Category>>>
    fun getAllProducts(): Flow<ResultState<List<Product>>>
    fun registerUser(user: User): Flow<ResultState<String>>
    fun loginUser(email: String, password: String): Flow<ResultState<String>>
    fun uploadImage(imageUri: Uri): Flow<ResultState<String>>
    fun getProductById(productId: String): Flow<ResultState<Product>>
    fun getUserData(): Flow<ResultState<User>>
    fun updateUserData(user: User): Flow<ResultState<String>>
    fun logoutUser(): Flow<ResultState<String>>
    fun addProductToCart(cart: Cart): Flow<ResultState<String>>
    fun getCartProducts(): Flow<ResultState<List<Cart>>>
    fun removeProductFromCart(productId: String): Flow<ResultState<String>>
    fun addToWishList(wish: Cart): Flow<ResultState<String>>
    fun getWishListProducts(): Flow<ResultState<List<Cart>>>
    fun removeProductFromWishList(productId: String): Flow<ResultState<String>>
    fun getProductsByCategory(category: String): Flow<ResultState<List<Product>>>
    fun getBanners(): Flow<ResultState<List<Banner>>>
    fun searchProducts(query: String): Flow<ResultState<List<Product>>>
    fun placeOrder(order: Order): Flow<ResultState<String>>
    suspend fun clearCart()
    suspend fun getOrders(userId: String): List<Order>
}