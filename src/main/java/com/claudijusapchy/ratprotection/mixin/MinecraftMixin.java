package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.SSIDScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(at = @At("RETURN"), method = "getUser", cancellable = true)
    private void onGetUser(CallbackInfoReturnable<User> cir) {
        if (SSIDScreen.session != null) cir.setReturnValue(SSIDScreen.session);
    }
}