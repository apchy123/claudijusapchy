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

    fun load() {
        if (!configFile.exists()) {
            save()
            return
        }
        runCatching {
            val json = gson.fromJson(configFile.readText(), JsonObject::class.java)
            TextShadowConfig.shadowEnabled = json.get("shadowEnabled")?.asBoolean ?: true
            PartyFinderRightClick.mode = json.get("partyFinderMode")?.asInt ?: 0
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
            json.addProperty("partyFinderMode", PartyFinderRightClick.mode)
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