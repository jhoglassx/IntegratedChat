package com.js.project.ui.auth

import com.js.project.domain.usecase.AuthGoogleUseCase
import com.js.project.domain.usecase.AuthTwitchUseCase
import com.js.project.provider.DispatcherProvider
import com.js.project.ui.auth.model.AuthAction
import com.js.project.ui.auth.model.AuthState
import com.js.project.ui.base.BaseViewModel
import io.github.aakira.napier.Napier
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
                Napier.e("AuthViewModel -> googleSignIn: $e")
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
                         Napier.i("AuthViewModel -> getGoogleUser -> user: $user")
                     }
                } catch (e: Exception){
                    Napier.e("AuthViewModel -> getGoogleUser: $e")
                }
            }
        }
    }

    private fun twitchSignIn(){
        viewModelScope.launch {
            try {
                val intent = authTwitchUseCase.signIn()
                _uiState.update {
                    it.copy(
                        authTwitchIntent = intent
                    )
                }
            } catch (e: Exception){
                Napier.e("AuthViewModel -> twitchSignIn: $e")
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
                        Napier.i("AuthViewModel -> getTwitchUser -> user: $user")
                    }
                } catch (e: Exception) {
                    Napier.e("AuthViewModel -> getTwitchUser: $e")
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