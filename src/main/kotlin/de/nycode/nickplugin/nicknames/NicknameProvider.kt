package de.nycode.nickplugin.nicknames

import de.nycode.nickplugin.model.Nickname

interface NicknameProvider {

    fun getNicknames(): List<Nickname>
    fun saveNickname(nickname: Nickname)

}