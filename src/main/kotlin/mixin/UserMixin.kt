package com.claudijusapchy.ratprotection.mixin

import com.claudijusapchy.ratprotection.ModLogger
import net.minecraft.client.User
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(User::class)
class UserMixin {

    // Trusted packages that are allowed to access the token
    private val trustedPackages = listOf(
        "net.minecraft",
        "net.fabricmc",
        "com.mojang",
        "com.claudijusapchy.ratprotection",
        "java.",
        "sun.",
        "jdk.",
        "kotlin.",
        "org.spongepowered"
    )

    @Inject(method = ["getAccessToken"], at = [At("HEAD")])
    fun onGetAccessToken(cir: CallbackInfoReturnable<String>) {
        checkCaller("getAccessToken")
    }

    @Inject(method = ["getProfileId"], at = [At("HEAD")])
    fun onGetProfileId(cir: CallbackInfoReturnable<Any>) {
        checkCaller("getProfileId")
    }

    private fun checkCaller(method: String) {
        val stack = Thread.currentThread().stackTrace

        // Walk up the call stack looking for suspicious callers
        for (frame in stack) {
            val className = frame.className

            // Skip trusted packages
            if (trustedPackages.any { className.startsWith(it) }) continue

            // Skip the mixin itself
            if (className.contains("UserMixin")) continue
            if (className.contains("ratprotection")) continue

            // Anything else calling this is suspicious
            ModLogger.block("[RatProtection] ALERT: Suspicious mod accessing $method!")
            ModLogger.block("[RatProtection] Caller: $className.${frame.methodName}(${frame.fileName}:${frame.lineNumber})")
            return
        }
    }
}