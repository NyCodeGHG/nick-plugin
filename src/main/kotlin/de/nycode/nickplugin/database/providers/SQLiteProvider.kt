package de.nycode.nickplugin.database.providers

import de.nycode.nickplugin.NickPlugin
import de.nycode.nickplugin.database.DatabaseProvider
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

object SQLiteProvider : DatabaseProvider {
    override val providerName = "SQLite"

    private var connection: Connection? = null

    override fun connect() {
        val config = NickPlugin.instance.config

        val databasePath = File(NickPlugin.instance.dataFolder, config.getString("database.file") ?: "sqlite.db")
        connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")
    }

    override fun close() {
        connection?.close()
    }
}