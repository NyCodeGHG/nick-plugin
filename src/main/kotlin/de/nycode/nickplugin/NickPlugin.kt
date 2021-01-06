package de.nycode.nickplugin

import de.nycode.nickplugin.commands.NickCommandExecutor
import de.nycode.nickplugin.commands.brigadier.BrigadierCompletions
import de.nycode.nickplugin.database.DatabaseConnector
import de.nycode.nickplugin.gui.NicknameInventory
import de.nycode.nickplugin.nicknames.NicknameLoader
import io.papermc.lib.PaperLib
import me.lucko.commodore.CommodoreProvider
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
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
        checkMetrics()
        printSystemInformation()
        DatabaseConnector.connect()
        NicknameLoader.currentProvider().reloadNicknames()
        registerCommands()
        registerListener()
    }

    override fun onDisable() {
        DatabaseConnector.currentProvider().close()
    }

    private fun printSystemInformation() {
        PaperLib.suggestPaper(this)
        slF4JLogger.info("---------- Nick Plugin by NyCode ----------")
        slF4JLogger.info("Current Version: ${description.version}-${BuildConfig.GIT_COMMIT}@${BuildConfig.GIT_BRANCH}")
        slF4JLogger.info("Latest Version: Not available")
        slF4JLogger.info("Minecraft Version: ${Bukkit.getMinecraftVersion()}")
        slF4JLogger.info("Server Brand: ${PaperLib.getEnvironment().name}")
        slF4JLogger.info("-------------------------------------------")
    }

    private fun checkMetrics() {
        val pluginId = 9886
        Metrics(this, pluginId)
    }

    private fun registerCommands() {
        val nickCommand = getCommand("nick") ?: error("Couldn't get nick command! This should not happen!")
        nickCommand.setExecutor(NickCommandExecutor())

        if (CommodoreProvider.isSupported()) {
            slF4JLogger.info("Detected 1.13+ Minecraft Version! Registering Brigadier Completions via Commodore!")
            val commodore = CommodoreProvider.getCommodore(this)
            BrigadierCompletions.registerCompletions(commodore, nickCommand)
        }
    }

    private fun registerListener() {
        val pluginManager = Bukkit.getPluginManager()
        pluginManager.registerEvents(NicknameInventory, this)
    }
}