package com.js.project.ui.auth

import com.js.project.domain.usecase.AuthGoogleUseCase
import com.js.project.domain.usecase.AuthTwitchUseCase
import com.js.project.provider.DispatcherProvider
import com.js.project.ui.auth.model.AuthAction
import com.js.project.ui.auth.model.AuthState
import com.js.project.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authGoogleUseCase: AuthGoogleUseCase,
    private val authTwitchUseCase: AuthTwitchUseCase,
    dispatcherProvider: DispatcherProvider
): BaseViewModel(
    dispatcherProvider = dispatcherProvider
) {

    private val _uiState = MutableStateFlow(AuthState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.GetGoogleSignInIntent -> googleSignIn()
            is AuthAction.GetTwitchSignInIntent -> twitchSignIn()
            is AuthAction.GetGoogleUser -> getGoogleUser(action.authorizationCode)
            is AuthAction.GetTwitchUser -> getTwitchUser(action.authorizationCode)
            is AuthAction.SignOut -> signOut()
        }
    }

    private fun googleSignIn()  {
        viewModelScope.launch {
            try {
                val intent = authGoogleUseCase.signIn()
                _uiState.update {
                    it.copy(
                        authGoogleIntent = intent
                    )
                }
            } catch (e: Exception){
                val error = e
            }
        }
    }

    private fun getGoogleUser(
        authorizationCode: String?,
    ) {
        viewModelScope.launch {
            authorizationCode?.let {
                try {
                     authGoogleUseCase.getUser(authorizationCode).collect { user ->
                         _uiState.update {
                             it.copy(
                                 userGoggle = user
                             )
                         }
                     }
                } catch (e: Exception){
                    _uiState.value = _uiState.value.copy(
                         //error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    private fun twitchSignIn(){
        viewModelScope.launch {
            val intent = authTwitchUseCase.signIn()
            _uiState.update {
                it.copy(
                    authTwitchIntent = intent
                )
            }
        }
    }

    private fun getTwitchUser(authorizationCode : String?) {
        viewModelScope.launch {
            authorizationCode?.let {
                try {
                    authTwitchUseCase.getUser(authorizationCode).collect { user ->
                        _uiState.update {
                            it.copy(
                                userTwitch = user
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        //error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    private fun signOut(){
        viewModelScope.launch {
            authTwitchUseCase.signOut()
            authGoogleUseCase.signOut()
        }
    }
}