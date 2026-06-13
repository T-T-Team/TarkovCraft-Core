package tnt.tarkovcraft.core.client.overlay;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoader;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.event.client.RegisterOnScreenHintEvent;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.client.config.OnScreenHintDisplay;
import tnt.tarkovcraft.core.client.hint.OnScreenHint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OnScreenHintLayer implements LayeredDraw.Layer {

    public static final ResourceLocation LAYER_ID = TarkovCraftCore.createIdentifier("layer/hint");
    private static final List<OnScreenHint> HINTS = new ArrayList<>();

    public OnScreenHintLayer() {
        ModLoader.postEvent(new RegisterOnScreenHintEvent(hint -> HINTS.add(Objects.requireNonNull(hint))));
    }

    public void tick() {
        HINTS.forEach(OnScreenHint::onHintUpdate);
    }

    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        OnScreenHintDisplay displayMode = TarkovCraftCoreClient.getConfig().hintDisplayMode;
        if (displayMode == OnScreenHintDisplay.NONE)
            return;
        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();
        Font font = client.font;
        int left = 2;
        int top = window.getGuiScaledHeight() - 2;
        int offset = 0;
        float delta = deltaTracker.getGameTimeDeltaPartialTick(false);
        for (OnScreenHint hint : HINTS) {
            if (hint.isDisabled())
                continue;
            if (displayMode.test(hint)) {
                int width = hint.getHintWidth(window, font);
                int height = hint.getHintHeight(window, font);
                int y = top - offset - height;
                graphics.enableScissor(left, y, left + width, y + height);
                hint.render(graphics, font, window, left, y, width, height, delta);
                graphics.disableScissor();
                offset += height;
            }
        }
    }
}
