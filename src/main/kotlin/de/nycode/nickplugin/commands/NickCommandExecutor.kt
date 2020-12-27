package de.nycode.nickplugin.commands

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import de.nycode.nickplugin.NickPlugin
import de.nycode.nickplugin.database.DatabaseConnector
import de.nycode.nickplugin.database.DatabaseProvider
import de.nycode.nickplugin.nicknames.NicknameLoader
import de.nycode.nickplugin.model.NickedPlayer
import de.nycode.nickplugin.model.Nickname
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.Bukkit

import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction


class NickCommandExecutor : CommandExecutor {

    private val provider = DatabaseConnector.currentProvider()
    private val protocolManager = ProtocolLibrary.getProtocolManager()

    init {
        protocolManager
            .addPacketListener(object : PacketAdapter(NickPlugin.instance, PacketType.Play.Server.PLAYER_INFO) {
                override fun onPacketSending(event: PacketEvent) {
                    val action = event.packet.playerInfoAction.read(0)
                    if (action !in arrayOf(PlayerInfoAction.ADD_PLAYER, PlayerInfoAction.REMOVE_PLAYER)) return

                    val playerInfoData = event.packet.playerInfoDataLists.read(0)
                    val newPlayerInfoData = mutableListOf<PlayerInfoData>()

                    for (data in playerInfoData) {
                        val nickedPlayer = provider.getNickedPlayer(data.profile.uuid)

                        when {
                            nickedPlayer == null
                                    || (!nickedPlayer.remove
                                    && action == PlayerInfoAction.REMOVE_PLAYER)
                                    || (nickedPlayer.remove && action == PlayerInfoAction.ADD_PLAYER) ->
                                newPlayerInfoData.add(data)

                            (!nickedPlayer.remove
                                    && action == PlayerInfoAction.ADD_PLAYER)
                                    || (nickedPlayer.remove && action == PlayerInfoAction.REMOVE_PLAYER) -> {
                                val fakeGameProfile = WrappedGameProfile(nickedPlayer.nickUUID, nickedPlayer.nickname)
                                val fakeData = PlayerInfoData(
                                    fakeGameProfile,
                                    data.latency,
                                    data.gameMode,
                                    WrappedChatComponent.fromText(nickedPlayer.nickname)
                                )
                                newPlayerInfoData.add(fakeData)
                            }

                        }
                    }
                    event.packet.playerInfoDataLists.write(0, newPlayerInfoData)
                }
            })
        protocolManager
            .addPacketListener(object : PacketAdapter(NickPlugin.instance, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
                override fun onPacketSending(event: PacketEvent) {
                    val nickedPlayer = provider.getNickedPlayer(event.packet.uuiDs.read(0)) ?: return

                    if (!nickedPlayer.remove) {
                        event.packet.uuiDs.write(0, nickedPlayer.nickUUID)
                    }
                }
            })
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return true
        }

        val provider = DatabaseConnector.currentProvider()

        if (provider.isPlayerNicked(sender.uniqueId)) {
            this.unnickPlayer(provider, sender)
            sender.sendMessage("Dein Nickname wurde entfernt")
        } else {
            val nickname = NicknameLoader.getNicknames()?.randomOrNull() ?: return true
            this.nickPlayer(provider, sender, nickname)
            sender.sendMessage("Du wurdest genickt als: ${nickname.name}")
        }
        return true
    }

    private fun nickPlayer(provider: DatabaseProvider, player: Player, nickname: Nickname) {
        provider.saveNickedPlayer(NickedPlayer(player.name, player.uniqueId, nickname.uuid, nickname.name))
        Bukkit.getOnlinePlayers().forEach {
            it.hidePlayer(NickPlugin.instance, player)
            it.showPlayer(NickPlugin.instance, player)
        }
    }

    private fun unnickPlayer(provider: DatabaseProvider, player: Player) {
        provider.setToBeRemovedPlayer(player.uniqueId)
        Bukkit.getOnlinePlayers().forEach {
            it.hidePlayer(NickPlugin.instance, player)
            it.showPlayer(NickPlugin.instance, player)
        }
        provider.removeNickedPlayer(player.uniqueId)
    }
}