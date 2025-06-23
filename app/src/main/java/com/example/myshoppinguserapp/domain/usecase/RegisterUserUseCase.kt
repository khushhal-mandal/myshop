package com.example.myshoppinguserapp.domain.usecase

import com.example.myshoppinguserapp.domain.model.User
import com.example.myshoppinguserapp.domain.repo.Repo
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repo: Repo
) {
    operator fun invoke(user: User) = repo.registerUser(user)
}