package com.claudijusapchy.ratprotection.config

import com.claudijusapchy.ratprotection.ModLogger
import com.claudijusapchy.ratprotection.TextShadowConfig
import com.claudijusapchy.ratprotection.features.CommandAliases
import com.claudijusapchy.ratprotection.features.PartyFinderRightClick
import com.claudijusapchy.ratprotection.features.ZoomFeature
import com.google.gson.Gson
import com.google.gson.JsonObject
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import com.claudijusapchy.ratprotection.features.UbikCubeTracker
import com.claudijusapchy.ratprotection.features.DisableWorldLoadingScreen

object ModConfig {

    private val gson = Gson()
    private val configFile: File = FabricLoader.getInstance()
        .configDir
        .resolve("ratprotection.json")
        .toFile()

    var copyLeaderKey: Int = -1
    var leavePartyKey: Int = -1
    var zoomKey: Int = -1
    var disableWorldLoadingScreen: Boolean = true

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
            DisableWorldLoadingScreen.enabled = disableWorldLoadingScreen
            UbikCubeTracker.enabled = json.get("ubikEnabled")?.asBoolean ?: true
            UbikCubeTracker.hudX = json.get("ubikHudX")?.asInt ?: 10
            UbikCubeTracker.hudY = json.get("ubikHudY")?.asInt ?: 10
            leavePartyKey = json.get("leavePartyKey")?.asInt ?: -1
            zoomKey = json.get("zoomKey")?.asInt ?: -1
            UbikCubeTracker.lastCompletedAt = json.get("ubikLastCompleted")?.asLong ?: 0L
            ZoomFeature.enabled = json.get("zoomEnabled")?.asBoolean ?: true
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
            json.addProperty("ubikLastCompleted", UbikCubeTracker.lastCompletedAt)
            json.addProperty("copyLeaderEnabled", PartyFinderRightClick.copyLeaderEnabled)
            disableWorldLoadingScreen = DisableWorldLoadingScreen.enabled
            json.addProperty("leavePartyEnabled", PartyFinderRightClick.leavePartyEnabled)
            json.addProperty("copyLeaderKey", copyLeaderKey)
            json.addProperty("leavePartyKey", leavePartyKey)
            json.addProperty("zoomKey", zoomKey)
            json.addProperty("ubikEnabled", UbikCubeTracker.enabled)
            json.addProperty("ubikHudX", UbikCubeTracker.hudX)
            json.addProperty("ubikHudY", UbikCubeTracker.hudY)
            json.addProperty("zoomEnabled", ZoomFeature.enabled)
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