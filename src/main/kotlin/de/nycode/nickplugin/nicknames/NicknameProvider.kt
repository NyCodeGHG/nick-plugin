package de.nycode.nickplugin.nicknames

import de.nycode.nickplugin.model.Nickname

interface NicknameProvider {

    val name: String

    fun getNicknames(): Set<Nickname>
    fun addNickname(nickname: Nickname)
    fun deleteNickname(nickname: Nickname)
    fun reloadNicknames()

}