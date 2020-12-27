package de.nycode.nickplugin.model

import java.util.*

data class NickedPlayer(
    val name: String,
    val uuid: UUID,
    val nickUUID: UUID,
    val nickname: String,
    var remove: Boolean = false
)