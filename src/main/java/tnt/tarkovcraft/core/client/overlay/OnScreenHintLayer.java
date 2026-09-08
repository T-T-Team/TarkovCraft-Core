package tnt.tarkovcraft.core.client.overlay;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.gui.GuiLayer;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.client.TarkovCraftCoreClient;
import tnt.tarkovcraft.core.client.config.OnScreenHintDisplay;
import tnt.tarkovcraft.core.client.hint.OnScreenHintManager;

public final class OnScreenHintLayer implements GuiLayer {

    public static final Identifier LAYER_ID = TarkovCraftCore.createIdentifier("layer/hint");

    private final OnScreenHintManager hintManager;

    public OnScreenHintLayer(OnScreenHintManager hintManager) {
        this.hintManager = hintManager;
    }

    public void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        OnScreenHintDisplay displayMode = TarkovCraftCoreClient.getConfig().hintDisplayMode;
        if (displayMode == OnScreenHintDisplay.NONE)
            return;
        float delta = deltaTracker.getGameTimeDeltaPartialTick(false);
        this.hintManager.extractRenderState(graphics, displayMode, delta);
    }
}
