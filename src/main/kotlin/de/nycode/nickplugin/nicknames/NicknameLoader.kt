package de.nycode.nickplugin.nicknames

import de.nycode.nickplugin.NickPlugin
import de.nycode.nickplugin.model.Nickname
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

object NicknameLoader {

    private var nicknames: List<Nickname>? = null
    private val logger = NickPlugin.instance.slF4JLogger

    fun load() {
        val config = NickPlugin.instance.config
        val nicknameFile =
            File(NickPlugin.instance.dataFolder, config.getString("nicknames.filename") ?: "nicknames.json")

        if (!nicknameFile.exists()) {
            NickPlugin.instance.saveResource("nicknames.json", false)
        }

        val json = nicknameFile.readText()
        nicknames = Json.decodeFromString<List<Nickname>>(json)
    }

    fun getNicknames() = nicknames

}