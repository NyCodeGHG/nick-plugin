package de.nycode.nickplugin

import de.nycode.nickplugin.database.DatabaseConnector
import org.bukkit.plugin.java.JavaPlugin

class NickPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: NickPlugin
            private set
    }

    override fun onLoad() {
        instance = this
        saveDefaultConfig()
    }

    override fun onEnable() {
        printSystemInformation()
        DatabaseConnector.connect()
    }

    private fun printSystemInformation() {
        slF4JLogger.info("Version: ${description.version}-${BuildConfig.GIT_COMMIT} on ${BuildConfig.GIT_BRANCH}")
    }
}