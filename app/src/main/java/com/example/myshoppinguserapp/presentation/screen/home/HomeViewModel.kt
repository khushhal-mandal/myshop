package com.example.myshoppinguserapp.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.Banner
import com.example.myshoppinguserapp.domain.model.Category
import com.example.myshoppinguserapp.domain.model.Product
import com.example.myshoppinguserapp.domain.usecase.GetAllCategoriesUseCase
import com.example.myshoppinguserapp.domain.usecase.GetAllProductsUseCase
import com.example.myshoppinguserapp.domain.usecase.GetBannersUseCase
import com.example.myshoppinguserapp.domain.usecase.SearchProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val getBannerUseCase: GetBannersUseCase,
    private val searchProductUseCase: SearchProductUseCase
) : ViewModel() {

    init {
        getAllCategories()
        getAllProducts()
        getBanners()
    }

    private val _getAllCategoriesState = MutableStateFlow(GetAllCategoriesState())
    val getAllCategoriesState = _getAllCategoriesState.asStateFlow()

     fun getAllCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllCategoriesUseCase().collect {
                when (it) {
                    is ResultState.Loading -> {
                        _getAllCategoriesState.value = GetAllCategoriesState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getAllCategoriesState.value = GetAllCategoriesState(categories = it.data)
                    }
                    is ResultState.Error -> {
                        _getAllCategoriesState.value = GetAllCategoriesState(error = it.message)
                    }
                }
            }
        }
    }


    private val _getAllProductsState = MutableStateFlow(GetAllProductsState())
    val getAllProductsState = _getAllProductsState.asStateFlow()

    private var _allProducts: List<Product> = emptyList()

     fun getAllProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllProductsUseCase().collect {
                when (it) {
                    is ResultState.Loading -> {
                        _getAllProductsState.value = GetAllProductsState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _allProducts = it.data
                        _getAllProductsState.value = GetAllProductsState(products = it.data)
                    }
                    is ResultState.Error -> {
                        _getAllProductsState.value = GetAllProductsState(error = it.message)
                    }
                }
            }
        }
    }


    private val _getBannerState = MutableStateFlow(GetBannerState())
    val getBannerState = _getBannerState.asStateFlow()

     fun getBanners() {
        viewModelScope.launch(Dispatchers.IO) {
            getBannerUseCase().collect {
                when (it) {
                    is ResultState.Loading -> {
                        _getBannerState.value = GetBannerState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _getBannerState.value = GetBannerState(banners = it.data)
                    }
                    is ResultState.Error -> {
                        _getBannerState.value = GetBannerState(error = it.message)
                    }
                }
            }
        }
    }


    private val _searchProductState = MutableStateFlow(SearchProductState())
    val searchProductState = _searchProductState.asStateFlow()

    fun searchProducts(query: String) {
        viewModelScope.launch {
            val lowercaseQuery = query.trim().lowercase()

            val filtered = _allProducts.filter {
                it.name.trim().lowercase().contains(lowercaseQuery)
            }

            _searchProductState.value = SearchProductState(products = filtered)
        }
    }

    fun resetSearchResult() {
        _searchProductState.value = SearchProductState(products = emptyList())
    }
}

data class GetAllCategoriesState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class GetAllProductsState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class GetBannerState(
    val banners: List<Banner> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class SearchProductState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
