package de.nycode.nickplugin.model

import de.nycode.nickplugin.serialization.UUIDAsStringSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Nickname(@Serializable(with = UUIDAsStringSerializer::class) val uuid: UUID, val name: String)
