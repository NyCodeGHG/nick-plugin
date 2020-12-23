package de.nycode.nickplugin.database

import de.nycode.nickplugin.NickPlugin
import de.nycode.nickplugin.database.providers.SQLiteProvider

object DatabaseConnector {

    private val providers = listOf<DatabaseProvider>(SQLiteProvider)
    private val logger = NickPlugin.instance.slF4JLogger
    private val provider: DatabaseProvider

    init {
        var providerName: String? = NickPlugin.instance.config.getString("database.provider")
        if (providerName == null) {
            logger.warn("Database Provider is missing, please check your config.yml! Using SQLite as default provider!")
            providerName = "SQLite"
        }

        var provider = providers.associateBy { it.providerName.toLowerCase() }[providerName.toLowerCase()]
        if (provider == null) {
            logger.warn("Database Provider '$providerName' is invalid, please check your config.yml! Using SQLite as default provider!")
            provider = providers.find { it.providerName == "sqlite" }

            if (provider == null) {
                throw IllegalStateException("SQLite provider is not available! This should not happen! Please report this error at https://github.com/NyCodeGHG/nick-plugin")
            }
        }
        this.provider = provider
    }

    fun connect() {
        try {
            provider.connect()
            logger.info("Successfully connected to the ${provider.providerName} database!")
        } catch (exception: Exception) {
            logger.error("An unknown error occurred whilst connecting to the database!", exception)
        }
    }

    fun currentProvider() = provider
}