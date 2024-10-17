import com.js.integratedchat.data.entity.BadgeResponse
import java.util.concurrent.ConcurrentHashMap

object Constants {
    //GOOGLE
    const val GOOGLE_REDIRECT_URI = "http://localhost:8081/callback"
    const val GOOGLE_SCOPES = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile openid https://www.googleapis.com/auth/youtube.readonly"
    const val GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token"
    const val GOOGLE_USER_URL = "https://www.googleapis.com/oauth2/v1/userinfo"
    var GOOGLE_TOKEN = ""
    var GOOGLE_LIVE_CHAT_ID: String = ""


    //TWITCH
    const val TWITCH_REDIRECT_URI = "http://localhost:8080/callback"
    const val TWITCH_SCOPES = "user:read:email chat:read chat:edit"
    const val TWITCH_TOKEN_URL = "https://id.twitch.tv/oauth2/token"
    const val TWITCH_USER_INFO_URL = "https://api.twitch.tv/helix/users"
    val TWITCH_BADGES_CACHE = ConcurrentHashMap<String, BadgeResponse>()
    var TWITCH_TOKEN = ""

    //EMOTE
    val EMOTE_REGEX = """:\w+(-\w+)*:""".toRegex()
}