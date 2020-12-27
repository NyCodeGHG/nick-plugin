package de.nycode.nickplugin.database

import de.nycode.nickplugin.model.NickedPlayer
import de.nycode.nickplugin.model.Nickname
import java.util.*

/**
 * Abstraction of a database source for usage of different databases
 */
interface DatabaseProvider {

    /**
     * Connect to the specific database
     */
    fun connect()

    /**
     * Close the connection to the database
     */
    fun close()

    /**
     * The name of the provider used in the config.yml configuration file
     */
    val providerName: String

    /**
     * Get a nicked player by it's uuid
     */
    fun getNickedPlayer(uuid: UUID): NickedPlayer?

    /**
     * Save a nicked player in the database
     */
    fun saveNickedPlayer(nickedPlayer: NickedPlayer)

    fun removeNickedPlayer(uuid: UUID)

    fun isNicknameInUse(nickname: Nickname): Boolean

    fun isPlayerNicked(uuid: UUID): Boolean {
        return getNickedPlayer(uuid) != null
    }

    fun setToBeRemovedPlayer(uniqueId: UUID)
}