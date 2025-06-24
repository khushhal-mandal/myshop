package com.example.myshoppinguserapp.domain.model

data class Order (
    val orderId: String = "",
    val time: Long = System.currentTimeMillis(),
    val products: List<Cart> = emptyList(),
    val totalPrice: Int = 0,
    val shippingInfo: ShippingInfo = ShippingInfo()
)
