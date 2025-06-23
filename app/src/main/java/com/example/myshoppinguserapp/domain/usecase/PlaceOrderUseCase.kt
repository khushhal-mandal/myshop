package com.example.myshoppinguserapp.domain.usecase

import com.example.myshoppinguserapp.domain.model.Order
import com.example.myshoppinguserapp.domain.repo.Repo
import javax.inject.Inject

class PlaceOrderUseCase @Inject constructor(
    private val repo: Repo
) {
    operator fun invoke(order: Order) = repo.placeOrder(order)
}