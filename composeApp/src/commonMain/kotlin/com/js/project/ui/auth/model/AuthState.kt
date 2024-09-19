package com.js.project.ui.auth.model

import com.js.project.domain.entity.UserEntity
import com.js.project.ui.base.BaseState

data class AuthState (
    val userTwitch: UserEntity? = null,
    val twitchCode: String? = null,
    var userGoggle: UserEntity? = null,
    var authGoogleIntent: Any? = null,
    var authTwitchIntent: Any? = null,
): BaseState()