package com.example.myshoppinguserapp.presentation.screen.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.Cart
import com.example.myshoppinguserapp.domain.usecase.GetWishListUseCase
import com.example.myshoppinguserapp.domain.usecase.RemoveProductFromWishListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishListViewModel @Inject constructor(
    private val getWishListUseCase: GetWishListUseCase,
    private val removeProductFromWishListUseCase: RemoveProductFromWishListUseCase
) : ViewModel() {
    init {
        getWishListProducts()
    }

    private val _wishListState = MutableStateFlow(WishListState())
    val wishListState = _wishListState.asStateFlow()

    fun getWishListProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            getWishListUseCase().collect { result ->
                when (result) {
                    is ResultState.Loading -> _wishListState.value = _wishListState.value.copy(isLoading = true)
                    is ResultState.Success -> _wishListState.value =
                        WishListState(data = result.data, isLoading = false)

                    is ResultState.Error -> _wishListState.value =
                        WishListState(error = result.message, isLoading = false)
                }
            }
        }
    }


    private val _removeMessage = MutableStateFlow<String?>(null)
    val removeMessage = _removeMessage.asStateFlow()

    fun removeProductFromWishList(productId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            removeProductFromWishListUseCase(productId).collect { result ->
                when (result) {
                    is ResultState.Loading -> _removeMessage.value = null
                    is ResultState.Success -> {
                        _removeMessage.value = result.data
                        getWishListProducts()
                    }
                    is ResultState.Error -> _removeMessage.value = result.message
                }
            }
        }
    }

    fun clearRemoveMessage() {
        _removeMessage.value = null
    }
}

data class WishListState(
    val isLoading: Boolean = false,
    val data: List<Cart> = emptyList(),
    val error: String? = null
)

