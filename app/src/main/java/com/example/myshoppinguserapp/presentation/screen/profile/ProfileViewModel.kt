package com.example.myshoppinguserapp.presentation.screen.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshoppinguserapp.common.resultstate.ResultState
import com.example.myshoppinguserapp.domain.model.User
import com.example.myshoppinguserapp.domain.usecase.GetUserDataUseCase
import com.example.myshoppinguserapp.domain.usecase.LogOutUserUseCase
import com.example.myshoppinguserapp.domain.usecase.LoginUserUseCase
import com.example.myshoppinguserapp.domain.usecase.RegisterUserUseCase
import com.example.myshoppinguserapp.domain.usecase.UpdateUserDataUseCase
import com.example.myshoppinguserapp.domain.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val updateUserDataUseCase: UpdateUserDataUseCase,
    private val logOutUserUseCase: LogOutUserUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {
    private val _userDataState = MutableStateFlow(UserDataState())
    val userDataState = _userDataState.asStateFlow()

    init {
        //getUserData()
    }

    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            getUserDataUseCase().collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _userDataState.value = UserDataState(isLoading = true)
                    }
                    is ResultState.Error -> {
                        _userDataState.value = UserDataState(error = it.message, isLoading = false)
                    }
                    is ResultState.Success -> {
                        _userDataState.value = UserDataState(user = it.data, isLoading = false)
                    }
                }
            }
        }
    }

    fun updateUserData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            updateUserDataUseCase(user).collectLatest {
                when (it) {
                    is ResultState.Loading -> {

                    }
                    is ResultState.Error -> {
                        _userDataState.value = _userDataState.value.copy(error = it.message)
                    }
                    is ResultState.Success -> {
                        _userDataState.value = _userDataState.value.copy(updated = it.data)
                    }
                }
            }
        }
    }

    fun clearUpdateMessage() {
        _userDataState.value = _userDataState.value.copy(updated = null)
    }

    fun logoutUser() {
        viewModelScope.launch(Dispatchers.IO) {
            logOutUserUseCase().collectLatest {
                when(it) {
                    is ResultState.Loading -> {
                        _userDataState.value = UserDataState(isLoading = true)
                    }
                    is ResultState.Error -> {
                        _userDataState.value = UserDataState(error = it.message, isLoading = false)
                        }
                    is ResultState.Success -> {
                        _userDataState.value = UserDataState(updated = it.data, isLoading = false)
                    }
                }
            }
        }
    }


    private val _registerUserState = MutableStateFlow(RegisterUserState())
    val registerUserState = _registerUserState.asStateFlow()

    fun registerUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            registerUserUseCase(user).collectLatest {
                when (it) {
                    is ResultState.Loading -> {
                        _registerUserState.value = RegisterUserState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _registerUserState.value = RegisterUserState(data = it.data, isLoading = false)
                    }
                    is ResultState.Error -> {
                        _registerUserState.value = RegisterUserState(error = it.message, isLoading = false)
                    }
                }
            }
        }
    }


    private val _loginUserState = MutableStateFlow(LoginUserState())
    val loginUserState = _loginUserState.asStateFlow()

    fun loginUser(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUserUseCase(email, password).collectLatest {
                when(it) {
                    is ResultState.Loading -> {
                        _loginUserState.value = LoginUserState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _loginUserState.value = LoginUserState(data = it.data, isLoading = false)
                    }
                    is ResultState.Error -> {
                        _loginUserState.value = LoginUserState(error = it.message, isLoading = false)
                    }
                }
            }
        }
    }


    private val _uploadImageState = MutableStateFlow(UploadImageState())
    val uploadImageState = _uploadImageState.asStateFlow()

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            uploadImageUseCase(imageUri).collectLatest {
                when(it) {
                    is ResultState.Loading -> {
                        _uploadImageState.value = UploadImageState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _uploadImageState.value = UploadImageState(data = it.data, isLoading = false)
                    }
                    is ResultState.Error -> {
                        _uploadImageState.value = UploadImageState(error = it.message, isLoading = false)
                    }
                }
            }
        }
    }

    fun resetImageState() {
        _uploadImageState.value = UploadImageState()
    }
}

data class UserDataState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val updated: String? = null,
    val error: String? = null
)

data class UploadImageState(
    val isLoading: Boolean = false,
    val data: String? = null,
    val error: String? = null
)


data class RegisterUserState(
    val isLoading: Boolean = false,
    val data: String? = null,
    val error: String? = null
)

data class LoginUserState(
    val isLoading: Boolean = false,
    val data: String? = null,
    val error: String? = null
)