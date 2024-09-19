package com.js.project.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
open class Navigation(
    val route: String
)

@Serializable
object ChatNavigation : Navigation(route = "Chat")
