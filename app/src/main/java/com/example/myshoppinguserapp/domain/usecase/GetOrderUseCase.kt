package com.example.myshoppinguserapp.domain.usecase

import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.Order
import com.example.myshoppinguserapp.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val repo: Repo,
    private val auth: FirebaseAuth
) {
    operator fun invoke(): Flow<ResultState<List<Order>>> = flow {
        emit(ResultState.Loading)

        val uid = auth.currentUser?.uid
        if (uid == null) {
            emit(ResultState.Error("User not logged in"))
            return@flow
        }

        try {
            val orders = repo.getOrders(uid)
            emit(ResultState.Success(orders))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "Unknown error"))
        }
    }
}
