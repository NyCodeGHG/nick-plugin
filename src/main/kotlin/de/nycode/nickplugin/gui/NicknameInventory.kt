package de.nycode.nickplugin.gui

import de.nycode.nickplugin.gui.SkullCreator.createPlayerHead
import de.nycode.nickplugin.nicknames.NicknameLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*

object NicknameInventory {

    private var nicknamePages = NicknameLoader.currentProvider().getNicknames().chunked(36)
    private val playerPages = mutableMapOf<UUID, Int>()

    fun open(player: Player) {
        val inventory = createInventory(player)

        player.openInventory(inventory)
    }

    private fun createInventory(player: Player): Inventory {
        val inventory = Bukkit.createInventory(null, 54, "Nicknames")

        if (!playerPages.containsKey(player.uniqueId)) {
            playerPages[player.uniqueId] = 1
        }
        drawPage(1, inventory)

        return inventory
    }

    private fun drawPage(pageNumber: Int, inventory: Inventory) {
        if (!canDrawPage(pageNumber)) return
        val page = nicknamePages[pageNumber - 1]
        page.forEachIndexed { index, nickname ->
            GlobalScope.launch {
                val head = createPlayerHead(nickname.uuid)
                inventory.setItem(index + 9, head)
            }
        }
    }

    private fun canDrawPage(page: Int) = page <= nicknamePages.size && page > 0

}