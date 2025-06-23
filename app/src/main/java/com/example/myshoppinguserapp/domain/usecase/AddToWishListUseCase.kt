package com.example.myshoppinguserapp.domain.usecase

import com.example.myshoppinguserapp.domain.model.Cart
import com.example.myshoppinguserapp.domain.repo.Repo
import javax.inject.Inject

class AddToWishListUseCase @Inject constructor(
    private val repo: Repo) {
    operator fun invoke(wish: Cart) = repo.addToWishList(wish)
}