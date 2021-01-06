package de.nycode.nickplugin.gui

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.gson.Gson
import com.google.gson.JsonObject
import de.nycode.nickplugin.gui.mojangapi.MojangSession
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import java.util.concurrent.TimeUnit

object SkullCreator {

    private val material = Material.matchMaterial("SKULL_ITEM") ?: Material.PLAYER_HEAD
    private val textureCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
        .build(object : CacheLoader<UUID, String>() {
            override fun load(key: UUID): String {
                return runBlocking {
                    val session =
                        client.get<MojangSession>("https://sessionserver.mojang.com/session/minecraft/profile/$key")

                    val decodedTexture =
                        String(Base64.getDecoder().decode(session.properties.get(0).asJsonObject.get("value").asString))
                    val json = gson.fromJson(decodedTexture, JsonObject::class.java)

                    val skinUrl = json.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").asString
                    val skinBytes = "{\"textures\":{\"SKIN\":{\"url\":\"$skinUrl\"}}}".toByteArray()

                    val headValue = String(Base64.getEncoder().encode(skinBytes))

                    val itemStack = ItemStack(material)

                    val meta = itemStack.itemMeta as SkullMeta
                    meta.owningPlayer = Bukkit.getOfflinePlayer(key)
                    itemStack.itemMeta = meta

                    val hashAsUUID = UUID(headValue.hashCode().toLong(), headValue.hashCode().toLong())
                    "{SkullOwner:{Id:\"$hashAsUUID\",Properties:{textures:[{Value:\"$headValue\"}]}}}"
                }
            }
        })

    private val gson = Gson()
    private val client = HttpClient(Apache) {
        Json {
            serializer = GsonSerializer()
        }
    }

    @Suppress("Deprecation")
    fun createPlayerHead(uuid: UUID): ItemStack {
        val itemStack = ItemStack(material)
        val meta = itemStack.itemMeta as SkullMeta
        meta.owningPlayer = Bukkit.getOfflinePlayer(uuid)

        return Bukkit.getUnsafe().modifyItemStack(
            itemStack,
            textureCache[uuid]
        )
    }

}