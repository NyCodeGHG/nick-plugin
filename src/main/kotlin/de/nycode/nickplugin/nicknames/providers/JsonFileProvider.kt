package de.nycode.nickplugin.nicknames.providers

import de.nycode.nickplugin.NickPlugin
import de.nycode.nickplugin.model.Nickname
import de.nycode.nickplugin.nicknames.NicknameProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.lang.Exception

object JsonFileProvider : NicknameProvider {
    override val name = "json-file"

    private val file = File(NickPlugin.instance.dataFolder, "nicknames.json")
    private var nicknames = emptySet<Nickname>()
    private val logger = NickPlugin.instance.slF4JLogger

    override fun getNicknames(): Set<Nickname> {
        return nicknames
    }

    override fun reloadNicknames() {
        try {
            val loadedNicknames: Set<Nickname> = Json.decodeFromString(file.bufferedReader().readText())
            this.nicknames = loadedNicknames
        } catch (exception: Exception) {
            logger.error("Unable to reload nicknames!", exception)
        }
    }

    override fun addNickname(nickname: Nickname) {
        val newNicknames = nicknames.toMutableSet()
        newNicknames.add(nickname)

        saveNicknames(newNicknames)
    }

    override fun deleteNickname(nickname: Nickname) {
        val newNicknames = nicknames.toMutableSet()
        newNicknames.remove(nickname)

        saveNicknames(newNicknames)
    }

    private fun saveNicknames(newNicknames: MutableSet<Nickname>) {
        try {
            val json = Json.encodeToString(newNicknames)
            file.writeText(json)
            this.nicknames = newNicknames
        } catch (exception: Exception) {
            logger.error("Unable to save nicknames!", exception)
        }
    }
}