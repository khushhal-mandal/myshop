package com.example.myshoppinguserapp.domain.usecase

import android.net.Uri
import com.example.myshoppinguserapp.domain.repo.Repo
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val repo: Repo
) {
    operator fun invoke(imageUri: Uri) = repo.uploadImage(imageUri)
}