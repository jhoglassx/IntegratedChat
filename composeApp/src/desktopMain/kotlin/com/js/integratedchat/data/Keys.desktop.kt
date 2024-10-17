package com.js.integratedchat.data

import com.js.integratedchat.BuildConfig

actual object Keys {
    actual val googleClientId: String = BuildConfig.GOOGLE_DESKTOP_CLIENT_ID
    actual val googleClientSecret: String = BuildConfig.GOOGLE_DESKTOP_CLIENT_SECRET
    actual val twitchClientId: String = BuildConfig.TWITCH_CLIENT_ID
    actual val twitchClientSecret: String = BuildConfig.TWITCH_CLIENT_SECRET
}

