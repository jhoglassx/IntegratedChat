package com.js.integratedchat.ui.auth.model

import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.ui.base.BaseState

data class AuthState (
    val userTwitch: UserEntity? = null,
    val twitchCode: String? = null,
    var userGoggle: UserEntity? = null,
    var authGoogleIntent: Any? = null,
    var authTwitchIntent: Any? = null,
): BaseState()