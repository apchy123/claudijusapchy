package com.claudijusapchy.ratprotection.mixin;

import com.claudijusapchy.ratprotection.events.EventBus;
import com.claudijusapchy.ratprotection.events.impl.ChatReceivedEvent;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatHudMixin {

    @Inject(method = "addMessage", at = @At("HEAD"))
    private void onAddMessage(Component message, CallbackInfo ci) {
        String stripped = message.getString();
        EventBus.INSTANCE.post(new ChatReceivedEvent(stripped));
    }
}