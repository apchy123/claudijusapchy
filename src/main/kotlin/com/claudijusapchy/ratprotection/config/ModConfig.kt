package com.claudijusapchy.ratprotection.config

import com.claudijusapchy.ratprotection.ModLogger
import com.claudijusapchy.ratprotection.TextShadowConfig
import com.claudijusapchy.ratprotection.features.CommandAliases
import com.claudijusapchy.ratprotection.features.PartyFinderRightClick
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.fabricmc.loader.api.FabricLoader
import java.io.File

object ModConfig {

    private val gson = Gson()
    private val configFile: File = FabricLoader.getInstance()
        .configDir
        .resolve("ratprotection.json")
        .toFile()

    var copyLeaderKey: Int = -1
    var leavePartyKey: Int = -1

    fun load() {
        if (!configFile.exists()) {
            save()
            return
        }
        runCatching {
            val json = gson.fromJson(configFile.readText(), JsonObject::class.java)
            TextShadowConfig.shadowEnabled = json.get("shadowEnabled")?.asBoolean ?: true
            PartyFinderRightClick.enabled = json.get("partyFinderEnabled")?.asBoolean ?: true
            PartyFinderRightClick.copyLeaderEnabled = json.get("copyLeaderEnabled")?.asBoolean ?: true
            PartyFinderRightClick.leavePartyEnabled = json.get("leavePartyEnabled")?.asBoolean ?: true
            copyLeaderKey = json.get("copyLeaderKey")?.asInt ?: -1
            leavePartyKey = json.get("leavePartyKey")?.asInt ?: -1
            val aliasObj = json.getAsJsonObject("aliases")
            aliasObj?.entrySet()?.forEach { entry ->
                CommandAliases.aliases[entry.key] = entry.value.asString
            }
            ModLogger.info("[RatProtection] Config loaded.")
        }.onFailure {
            ModLogger.warn("[RatProtection] Failed to load config: ${it.message}")
        }
    }

    fun save() {
        runCatching {
            val json = JsonObject()
            json.addProperty("shadowEnabled", TextShadowConfig.shadowEnabled)
            json.addProperty("partyFinderEnabled", PartyFinderRightClick.enabled)
            json.addProperty("copyLeaderEnabled", PartyFinderRightClick.copyLeaderEnabled)
            json.addProperty("leavePartyEnabled", PartyFinderRightClick.leavePartyEnabled)
            json.addProperty("copyLeaderKey", copyLeaderKey)
            json.addProperty("leavePartyKey", leavePartyKey)
            val aliasObj = JsonObject()
            CommandAliases.aliases.forEach { (alias, cmd) ->
                aliasObj.addProperty(alias, cmd)
            }
            json.add("aliases", aliasObj)
            configFile.writeText(gson.toJson(json))
        }.onFailure {
            ModLogger.warn("[RatProtection] Failed to save config: ${it.message}")
        }
    }
}