package com.js.integratedchat.ui.auth

import co.touchlab.kermit.Logger
import com.js.integratedchat.domain.usecase.AuthGoogleUseCase
import com.js.integratedchat.domain.usecase.AuthTwitchUseCase
import com.js.integratedchat.domain.usecase.UserGoogleUseCase
import com.js.integratedchat.domain.usecase.UserTwitchUseCase
import com.js.integratedchat.ext.error
import com.js.integratedchat.ext.info
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.ui.auth.model.AuthAction
import com.js.integratedchat.ui.auth.model.AuthState
import com.js.integratedchat.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authGoogleUseCase: AuthGoogleUseCase,
    private val authTwitchUseCase: AuthTwitchUseCase,
    private val userGoogleUseCase: UserGoogleUseCase,
    private val userTwitchUseCase: UserTwitchUseCase,
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
            } catch (e: Exception) {
                Logger.error(
                    tag = "AuthViewModel",
                    throwable = e,
                    message = "googleSignIn: $e"
                )
            }
        }
    }

    private fun getGoogleUser(
        authorizationCode: String?,
    ) {
        viewModelScope.launch {
            authorizationCode?.let {
                try {
                    userGoogleUseCase.getUser(authorizationCode).collect { user ->
                         _uiState.update {
                             it.copy(
                                 userGoggle = user
                             )
                         }
                         Logger.info("AuthViewModel","getGoogleUser -> user: $user")
                     }
                } catch (e: Exception){
                    Logger.error(
                        tag = "AuthViewModel",
                        throwable = e,
                        message = "getGoogleUser: $e"
                    )
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
                Logger.error(
                    tag = "AuthViewModel",
                    throwable = e,
                    message = "twitchSignIn: $e"
                )
            }
        }
    }

    private fun getTwitchUser(authorizationCode : String?) {
        viewModelScope.launch {
            authorizationCode?.let {
                try {
                    userTwitchUseCase.getUser(authorizationCode).collect { user ->
                        _uiState.update {
                            it.copy(
                                userTwitch = user
                            )
                        }
                        Logger.info("AuthViewModel","getTwitchUser -> user: $user")
                    }
                } catch (e: Exception) {
                    Logger.error(
                        tag = "AuthViewModel",
                        throwable = e,
                        message = "getTwitchUser: $e"
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