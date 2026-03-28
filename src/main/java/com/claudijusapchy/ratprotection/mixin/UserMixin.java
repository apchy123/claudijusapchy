package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.ModLogger;
import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.List;

@Mixin(User.class)
public class UserMixin {

    private static final List<String> TRUSTED = List.of(
            "net.minecraft", "net.fabricmc", "com.mojang",
            "com.claudijusapchy.ratprotection", "java.", "sun.",
            "jdk.", "kotlin.", "org.spongepowered", "org.prismlauncher",
            "net.caffeinemc", "me.owdding"
    );

    @Inject(method = "getAccessToken", at = @At("HEAD"))
    private void onGetAccessToken(CallbackInfoReturnable<String> cir) {
        checkCaller("getAccessToken");
    }

    @Inject(method = "getProfileId", at = @At("HEAD"))
    private void onGetProfileId(CallbackInfoReturnable<java.util.UUID> cir) {
        checkCaller("getProfileId");
    }

    private void checkCaller(String method) {
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            String cls = frame.getClassName();
            if (TRUSTED.stream().anyMatch(cls::startsWith)) continue;
            if (cls.contains("UserMixin") || cls.contains("ratprotection")) continue;
            ModLogger.INSTANCE.block("[RatProtection] ALERT: Suspicious access to " + method + " by " + cls);
            return;
        }
    }
}