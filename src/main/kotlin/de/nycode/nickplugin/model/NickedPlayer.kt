package de.nycode.nickplugin.model

import de.nycode.nickplugin.serialization.UUIDAsLongSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable(with = UUIDAsLongSerializer::class)
data class NickedPlayer(val name: String, val uuid: UUID, val nickUUID: UUID, val nickname: String)