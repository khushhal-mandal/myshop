package com.example.myshoppinguserapp.domain.model

data class User (
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val image: String = "",
    val address: String = ""
)