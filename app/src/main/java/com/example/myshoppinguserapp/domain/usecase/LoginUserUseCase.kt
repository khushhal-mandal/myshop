package com.example.myshoppinguserapp.domain.usecase

import com.example.myshoppinguserapp.domain.repo.Repo
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repo: Repo
) {
    operator fun invoke(email: String, password: String) = repo.loginUser(email, password)
}