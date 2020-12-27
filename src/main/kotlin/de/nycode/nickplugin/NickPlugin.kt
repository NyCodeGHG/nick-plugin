package de.nycode.nickplugin

import de.nycode.nickplugin.commands.NickCommandExecutor
import de.nycode.nickplugin.database.DatabaseConnector
import de.nycode.nickplugin.nicknames.NicknameLoader
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
        NicknameLoader.currentProvider().reloadNicknames()
        registerCommands()
    }

    override fun onDisable() {
        DatabaseConnector.currentProvider().close()
    }

    private fun printSystemInformation() {
        slF4JLogger.info("Version: ${description.version}-${BuildConfig.GIT_COMMIT}@${BuildConfig.GIT_BRANCH}")
    }

    private fun registerCommands() {
        getCommand("nick")?.setExecutor(NickCommandExecutor())
    }
}