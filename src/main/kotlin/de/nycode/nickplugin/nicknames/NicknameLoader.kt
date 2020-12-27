package de.nycode.nickplugin.nicknames

import de.nycode.nickplugin.NickPlugin
import de.nycode.nickplugin.nicknames.providers.JsonFileProvider

object NicknameLoader {

    private val providers: List<NicknameProvider> = listOf(JsonFileProvider)
    private val logger = NickPlugin.instance.slF4JLogger
    private val provider: NicknameProvider

    init {
        val config = NickPlugin.instance.config
        var providerName = config.getString("nicknames.provider")
        if (providerName == null) {
            logger.warn("Could not find nickname provider name! Using json-file as fallback")
            providerName = "json-file"
        }

        provider = providers.find { it.name.equals(providerName, ignoreCase = true) } ?: JsonFileProvider
    }

    fun currentProvider() = provider

}