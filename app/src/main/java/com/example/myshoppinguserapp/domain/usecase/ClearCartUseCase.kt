package com.example.myshoppinguserapp.domain.usecase

import com.example.myshoppinguserapp.domain.repo.Repo
import javax.inject.Inject

class ClearCartUseCase @Inject constructor(
    private val repo: Repo
) {
    suspend operator fun invoke() {
            repo.clearCart()
    }
}