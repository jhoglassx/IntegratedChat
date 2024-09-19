package com.js.project.service

import com.js.project.provider.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class TwitchChatServiceImpl(
    private val dispatcherProvider : DispatcherProvider,
): TwitchChatService {
    override suspend fun getTwitchChatMessages(
        channel: String,
        twitchToken: String,
        twitchUsername: String
    ): Flow<String> = flow {
        val socket = Socket("irc.chat.twitch.tv", 6667)
        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        val output = OutputStreamWriter(socket.getOutputStream())

        try {
            output.write("PASS oauth:$twitchToken\r\n")
            output.write("NICK your_twitch_username\r\n")
            output.write("CAP REQ :twitch.tv/tags twitch.tv/commands twitch.tv/membership\r\n")
            output.write("JOIN #$channel\r\n")
            output.flush()

            while (true) {
                val line = input.readLine()
                if (line != null) {
                    emit(line)
                    println("Emitted String message: $line") // Confirm emission
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("TwitchChatService: $e")
        } finally {
            input.close()
            output.close()
            socket.close()
        }
    }.flowOn(dispatcherProvider.IO)
}