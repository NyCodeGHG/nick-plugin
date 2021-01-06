package de.nycode.nickplugin.gui

import de.nycode.nickplugin.gui.SkullCreator.createPlayerHead
import de.nycode.nickplugin.model.Nickname
import de.nycode.nickplugin.nicknames.NicknameLoader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

object NicknameInventory : Listener {

    private var nicknamePages = NicknameLoader.currentProvider().getNicknames().sortedBy { it.name }.chunked(36)
    private val playerPages = mutableMapOf<UUID, Int>()
    private val inventories = mutableListOf<Inventory>()

    private val addNicknameItem = ItemStack(Material.LIME_DYE)
    private val placeholder = ItemStack(Material.BLUE_STAINED_GLASS_PANE)

    init {
        val addNicknameMeta = addNicknameItem.itemMeta
        addNicknameMeta.setDisplayName("${GREEN}Nickname hinzufügen")
        addNicknameMeta.lore = listOf("${GRAY}Klicke um einen Nickname hinzuzufügen.")
        addNicknameItem.itemMeta = addNicknameMeta

        val placeholderMeta = placeholder.itemMeta
        placeholderMeta.setDisplayName(" ")
        placeholder.itemMeta = placeholderMeta
    }

    fun open(player: Player) {
        val inventory = createInventory(player)

        player.openInventory(inventory)
        inventories.add(inventory)
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

        repeat(54) { current ->
            inventory.setItem(current, placeholder)
        }

        inventory.setItem(8, addNicknameItem)

        page.forEachIndexed { index, nickname ->
            GlobalScope.launch {
                val head = createPlayerHead(nickname.uuid)
                setNicknameMeta(head, nickname)
                inventory.setItem(index + 9, head)
            }
        }
    }

    private fun setNicknameMeta(head: ItemStack, nickname: Nickname) {
        val meta = head.itemMeta
        meta.setDisplayName("$YELLOW${nickname.name}")
        meta.lore = listOf(
            "", "${GOLD}Nickname:",
            "$GRAY${nickname.name}",
            "${GOLD}UUID:",
            "${GRAY}${nickname.uuid}",
            "",
            "${BLUE}Linkslick",
            "${GRAY}um diesen Nickname zu ${RED}bearbeiten",
            "",
            "${BLUE}Rechtsklick",
            "${GRAY}um diesen Nickname zu ${GREEN}verwenden"
        )
        head.itemMeta = meta
    }

    private fun canDrawPage(page: Int) = page <= nicknamePages.size && page > 0

    @EventHandler
    private fun onInventoryClick(event: InventoryClickEvent) {
        val inventory = event.inventory
        if (inventory in this.inventories) {
            event.result = Event.Result.DENY
        }
    }

    @EventHandler
    private fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory
        this.inventories.remove(inventory)
    }
}