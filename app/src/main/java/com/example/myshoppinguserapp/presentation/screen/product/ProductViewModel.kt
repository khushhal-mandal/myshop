package com.example.myshoppinguserapp.presentation.screen.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.Cart
import com.example.myshoppinguserapp.domain.model.Product
import com.example.myshoppinguserapp.domain.usecase.AddToCartUseCase
import com.example.myshoppinguserapp.domain.usecase.AddToWishListUseCase
import com.example.myshoppinguserapp.domain.usecase.GetProductByIdUseCase
import com.example.myshoppinguserapp.domain.usecase.GetProductsByCategoryUseCase
import com.example.myshoppinguserapp.domain.usecase.RemoveProductFromWishListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val addToWishListUseCase: AddToWishListUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val removeProductFromWishListUseCase: RemoveProductFromWishListUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase
) : ViewModel() {

    private val _addToCartState = MutableStateFlow<String?>(null)
    val addToCartState = _addToCartState.asStateFlow()

    fun addToCart(cart: Cart) {
        viewModelScope.launch(Dispatchers.IO) {
            addToCartUseCase(cart).collect { result ->
                _addToCartState.value = when (result) {
                    is ResultState.Loading -> null
                    is ResultState.Success -> result.data
                    is ResultState.Error -> result.message
                }
            }
        }
    }

    fun clearAddToCartState() {
        _addToCartState.value = null
    }


    private val _addToWishListState = MutableStateFlow<String?>(null)
    val addToWishListState = _addToWishListState.asStateFlow()

    fun addToWishList(cart: Cart) {
        viewModelScope.launch(Dispatchers.IO) {
            addToWishListUseCase(cart).collect { result ->
                _addToWishListState.value = when (result) {
                    is ResultState.Loading -> null
                    is ResultState.Success -> result.data
                    is ResultState.Error -> result.message
                }
            }
        }
    }

    fun clearAddToWishListState() {
        _addToWishListState.value = null
    }


    private val _removeMessage = MutableStateFlow<String?>(null)
    val removeMessage = _removeMessage.asStateFlow()

    fun removeProductFromWishList(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            removeProductFromWishListUseCase(productId).collect { result ->
                _removeMessage.value = when (result) {
                    is ResultState.Loading -> null
                    is ResultState.Success -> result.data
                    is ResultState.Error -> result.message
                }
            }
        }
    }

    fun clearRemoveMessage() {
        _removeMessage.value = null
    }


    private val _getProductByIdState = MutableStateFlow(ProductDetailsState())
    val getProductByIdState = _getProductByIdState.asStateFlow()

    fun getProductById(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getProductByIdUseCase(productId).collect {
                when (it) {
                    is ResultState.Loading -> {
                        _getProductByIdState.value = ProductDetailsState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getProductByIdState.value = ProductDetailsState(product = it.data, isLoading = false)
                    }
                    is ResultState.Error -> {
                        _getProductByIdState.value = ProductDetailsState(error = it.message, isLoading = false)
                    }
                }
            }
        }
    }


    private val _getProductsByCategoryState = MutableStateFlow(GetAllProductsState())
    val getProductsByCategoryState = _getProductsByCategoryState.asStateFlow()

    fun getProductsByCategory(category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getProductsByCategoryUseCase(category).collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getProductsByCategoryState.value = GetAllProductsState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getProductsByCategoryState.value = GetAllProductsState(
                            products = result.data,
                            isLoading = false
                        )
                    }
                    is ResultState.Error -> {
                        _getProductsByCategoryState.value = GetAllProductsState(
                            error = result.message,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }
}

data class ProductDetailsState(
    val product: Product? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)

data class GetAllProductsState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)