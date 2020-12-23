package de.nycode.nickplugin.commands

import de.nycode.nickplugin.database.DatabaseConnector
import de.nycode.nickplugin.io.NicknameLoader
import de.nycode.nickplugin.model.NickedPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NickCommandExecutor : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return true
        }

        val provider = DatabaseConnector.currentProvider()

        if (provider.isPlayerNicked(sender.uniqueId)) {
            provider.removeNickedPlayer(sender.uniqueId)
            sender.sendMessage("Dein Nickname wurde entfernt!")
        } else {
            val nickname = NicknameLoader.getNicknames()?.randomOrNull() ?: return true
            provider.saveNickedPlayer(NickedPlayer(sender.name, sender.uniqueId, nickname.uuid, nickname.name))
            sender.sendMessage("Du wurdest genickt als: ${nickname.name}")
        }
        return true
    }
}