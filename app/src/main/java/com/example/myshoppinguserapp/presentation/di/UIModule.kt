package com.example.myshoppinguserapp.presentation.di

import com.example.myshoppinguserapp.data.repoimpl.RepoImpl
import com.example.myshoppinguserapp.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UIModule {
    @Provides
    @Singleton
    fun provideRepo(firestore: FirebaseFirestore, auth: FirebaseAuth, storage: FirebaseStorage): Repo {
        return RepoImpl(firestore = firestore, auth = auth, storage = storage)
    }
}