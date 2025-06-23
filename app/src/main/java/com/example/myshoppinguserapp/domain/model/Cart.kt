package com.example.myshoppinguserapp.domain.model


data class Cart(
    val productId: String = "",
    val productName: String = "",
    val productImage: String = "",
    val size: String = "",
    val color: String = "",
    val quantity: String = "",
    val totalPrice: String = "",
    val category: String = ""
)