package de.nycode.nickplugin.commands.brigadier

import com.mojang.brigadier.tree.LiteralCommandNode
import de.nycode.nickplugin.NickPlugin
import me.lucko.commodore.Commodore
import me.lucko.commodore.file.CommodoreFileFormat
import org.bukkit.command.PluginCommand

object BrigadierCompletions {
    fun registerCompletions(commodore: Commodore, command: PluginCommand) {
        val nickCommand = CommodoreFileFormat.parse<LiteralCommandNode<*>>(NickPlugin.instance.getResource("nick.commodore"))
        commodore.register(command, nickCommand)
    }
}