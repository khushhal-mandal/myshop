package com.example.myshoppinguserapp.presentation.screen.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.Cart
import com.example.myshoppinguserapp.domain.model.Order
import com.example.myshoppinguserapp.domain.usecase.ClearCartUseCase
import com.example.myshoppinguserapp.domain.usecase.GetCartUseCase
import com.example.myshoppinguserapp.domain.usecase.GetOrdersUseCase
import com.example.myshoppinguserapp.domain.usecase.PlaceOrderUseCase
import com.example.myshoppinguserapp.domain.usecase.RemoveFromCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    init {
        getCart()
    }


    private val _getCartState = MutableStateFlow(GetCartState())
    val getCartState = _getCartState.asStateFlow()

    fun getCart() {
        viewModelScope.launch(Dispatchers.IO) {
            getCartUseCase().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getCartState.value = GetCartState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getCartState.value = GetCartState(cart = result.data)
                    }
                    is ResultState.Error -> {
                        _getCartState.value = GetCartState(error = result.message)
                    }
                }
            }
        }
    }


    private val _removeFromCartState = MutableStateFlow<String?>(null)
    val removeFromCartState = _removeFromCartState.asStateFlow()

    private val _isRemoving = MutableStateFlow(false)
    val isRemoving = _isRemoving.asStateFlow()

    fun removeFromCart(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            removeFromCartUseCase(productId).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _isRemoving.value = true
                        _removeFromCartState.value = null
                    }
                    is ResultState.Success -> {
                        _isRemoving.value = false
                        _removeFromCartState.value = result.data
                        getCart()
                    }
                    is ResultState.Error -> {
                        _isRemoving.value = false
                        _removeFromCartState.value = result.message
                    }
                }
            }
        }
    }

    fun clearRemoveFromCartState() {
        _removeFromCartState.value = null
    }


    private val _placeOrderState = MutableStateFlow(PlaceOrderState())
    val placeOrderState = _placeOrderState.asStateFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch {
            placeOrderUseCase(order).collect {
                when (it) {
                    is ResultState.Loading -> _placeOrderState.value = PlaceOrderState(isLoading = true)
                    is ResultState.Success -> _placeOrderState.value = PlaceOrderState(successMessage = it.data)
                    is ResultState.Error -> _placeOrderState.value = PlaceOrderState(error = it.message)
                }
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
            _getCartState.value = GetCartState(cart = emptyList())
        }
    }

    private val _orderState = MutableStateFlow(OrderState())
    val orderState = _orderState.asStateFlow()

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        viewModelScope.launch {
            getOrdersUseCase().collect { result ->
                when (result) {
                    is ResultState.Loading -> _orderState.value = OrderState(isLoading = true)
                    is ResultState.Success -> _orderState.value = OrderState(orders = result.data)
                    is ResultState.Error -> _orderState.value = OrderState(error = result.message)
                }
            }
        }
    }

}

data class GetCartState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val cart: List<Cart> = emptyList()
)

data class PlaceOrderState(
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

data class OrderState(
    val isLoading: Boolean = false,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)