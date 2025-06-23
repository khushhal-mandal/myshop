package com.example.myshoppinguserapp.domain.usecase

import com.example.myshoppinguserapp.domain.repo.Repo
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repo: Repo
) {
    operator fun invoke() = repo.getUserData()
}