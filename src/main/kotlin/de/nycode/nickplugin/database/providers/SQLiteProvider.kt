package de.nycode.nickplugin.database.providers

import de.nycode.nickplugin.NickPlugin
import de.nycode.nickplugin.database.DatabaseProvider
import de.nycode.nickplugin.model.NickedPlayer
import de.nycode.nickplugin.model.Nickname
import java.io.File
import java.nio.ByteBuffer
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

object SQLiteProvider : DatabaseProvider {
    override val providerName = "SQLite"
    private var connection: Connection? = null

    override fun connect() {
        val config = NickPlugin.instance.config

        val databasePath = File(NickPlugin.instance.dataFolder, config.getString("database.file") ?: "sqlite.db")
        connection = DriverManager.getConnection("jdbc:sqlite:$databasePath")
            ?: throw IllegalStateException("SQLite connection error")

        val statement = connection?.createStatement()
        statement?.executeUpdate(
            "CREATE TABLE IF NOT EXISTS nicks" +
                    "(" +
                    "    uuid      blob PRIMARY KEY," +
                    "    name      VARCHAR(16) NOT NULL," +
                    "    nick_uuid blob        NOT NULL," +
                    "    nick_name VARCHAR(16) NOT NULL" +
                    ");"
        )
    }

    override fun close() {
        connection?.close()
        connection = null
    }

    override fun getNickedPlayer(uuid: UUID): NickedPlayer? {
        val statement = connection?.prepareStatement("SELECT * FROM nicks WHERE uuid = ?")

        statement?.setBytes(1, uuid.toByteArray())

        val result = statement?.executeQuery()
        if (result?.next() == true) {
            val name = result.getString("name")
            val nickUUID = result.getBytes("nick_uuid").toUUID()
            val nickname = result.getString("nick_name")

            return NickedPlayer(name, uuid, nickUUID, nickname)
        }
        return null
    }

    override fun saveNickedPlayer(nickedPlayer: NickedPlayer) {
        val statement =
            connection?.prepareStatement("INSERT INTO nicks (uuid, name, nick_uuid, nick_name) VALUES (?, ?, ?, ?)")

        statement?.setBytes(1, nickedPlayer.uuid.toByteArray())
        statement?.setString(2, nickedPlayer.name)
        statement?.setBytes(3, nickedPlayer.nickUUID.toByteArray())
        statement?.setString(4, nickedPlayer.nickname)

        statement?.executeUpdate()
    }

    override fun removeNickedPlayer(uuid: UUID) {
        val statement =
            connection?.prepareStatement("DELETE FROM nicks WHERE uuid = ?")

        statement?.setBytes(1, uuid.toByteArray())

        statement?.executeUpdate()
    }

    override fun isNicknameInUse(nickname: Nickname): Boolean {
        TODO("Not yet implemented")
    }
}

fun UUID.toByteArray(): ByteArray {
    val buffer = ByteBuffer.wrap(ByteArray(16))
    buffer.putLong(this.mostSignificantBits)
    buffer.putLong(this.leastSignificantBits)
    return buffer.array()
}

fun ByteArray.toUUID(): UUID {
    val buffer = ByteBuffer.wrap(this)
    val most = buffer.long
    val least = buffer.long
    return UUID(most, least)
}