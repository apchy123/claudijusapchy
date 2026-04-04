package com.claudijusapchy.ratprotection;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import static java.util.concurrent.CompletableFuture.runAsync;

public class SSIDScreen extends Screen {
    private static SSIDScreen instance;
    public static User session;
    private final Minecraft mc = Minecraft.getInstance();
    private final Screen parent;
    private EditBox SSIDField;
    private String feedBackMessage = "";
    private int feedBackColor = -1;
    private int centerX = 0;
    private int centerY = 0;
    private final int FIELD_WIDTH = 200;

    private SSIDScreen(Screen parent) {
        super(Component.literal("SSID"));
        this.parent = parent;
    }

    public static SSIDScreen getInstance() {
        if (instance == null) instance = new SSIDScreen(null);
        return instance;
    }

    @Override
    public void onClose() { mc.setScreen(this.parent); }

    @Override
    protected void init() {
        StringWidget ssidText = new StringWidget(Component.literal("SSID"), mc.font);
        this.centerX = this.width / 2 - (FIELD_WIDTH / 2);
        this.centerY = 60;
        this.SSIDField = new EditBox(mc.font, FIELD_WIDTH, 20,
                (session == null) ? Component.empty() : Component.literal(session.getAccessToken()));
        ssidText.setWidth(FIELD_WIDTH);
        ssidText.setPosition(this.centerX, this.centerY + 35);
        this.SSIDField.setPosition(this.centerX, this.centerY + 45);
        this.SSIDField.setMaxLength(10000);
        this.addRenderableWidget(ssidText);
        this.addRenderableWidget(this.SSIDField);
        this.addRenderableWidget(Button.builder(Component.literal("Login"), button -> login())
                .width(FIELD_WIDTH).pos(this.centerX, this.centerY + 70).build());
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), button -> reset())
                .width(FIELD_WIDTH).pos(this.centerX, this.centerY + 95).build());
        this.addRenderableWidget(Button.builder(Component.literal("Copy SSID"), button -> copySSID())
                .width(FIELD_WIDTH).pos(this.centerX, this.centerY + 120).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float deltaTicks) {
        graphics.drawString(mc.font, this.feedBackMessage,
                this.centerX + (FIELD_WIDTH / 2) - (mc.font.width(this.feedBackMessage) >> 1),
                this.centerY, this.feedBackColor, true);
        String currentUser = "Current Account : " + mc.getUser().getName();
        graphics.drawString(mc.font, currentUser,
                this.centerX + (FIELD_WIDTH / 2) - (mc.font.width(currentUser) >> 1),
                this.centerY + 10, -1, true);
        super.render(graphics, mouseX, mouseY, deltaTicks);
    }

    private void login() {
        if (this.SSIDField.getValue().isEmpty()) {
            this.feedBackMessage = "Please enter a SSID!";
            this.feedBackColor = -7405568;
            return;
        }
        String ssidText = this.SSIDField.getValue().trim();
        this.feedBackMessage = "Logging in...";
        this.feedBackColor = -1;
        runAsync(() -> {
            String[] info = null;
            boolean failed = false;
            for (int i = 0; i < 10; i++) {
                try {
                    info = Utils.getProfileInfo(ssidText);
                    break;
                } catch (Exception e) {
                    if (i == 9) {
                        this.feedBackMessage = "Ran out of retries, network error!";
                        this.feedBackColor = -7405568;
                        failed = true;
                    }
                }
            }
            if (failed || info == null) return;
            try {
                session = new User(info[0], Utils.stringToUUID(info[1]), ssidText,
                        Optional.empty(), Optional.empty());
                this.feedBackMessage = "Successfully updated session!";
                this.feedBackColor = -16739323;
            } catch (Exception e) {
                this.feedBackMessage = "Failed to parse UUID from string!";
                this.feedBackColor = -7405568;
            }
        });
    }

    public static void reset() { session = null; }

    public static void copySSID() {
        Minecraft.getInstance().keyboardHandler.setClipboard(Minecraft.getInstance().getUser().getAccessToken());
    }
}