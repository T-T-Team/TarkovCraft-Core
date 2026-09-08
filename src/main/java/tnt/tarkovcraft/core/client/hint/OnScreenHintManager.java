package tnt.tarkovcraft.core.client.hint;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHint;
import tnt.tarkovcraft.core.api.client.hint.OnScreenHintRenderer;
import tnt.tarkovcraft.core.api.client.hint.TextHint;
import tnt.tarkovcraft.core.client.config.OnScreenHintDisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public final class OnScreenHintManager {

    private final List<HintRenderContainer<?>> hints = new ArrayList<>(5);
    private final List<HintRenderContainer<?>> actionHints = new ArrayList<>(3);

    public <T extends OnScreenHint> void registerHint(T hint) {
        OnScreenHintRenderer<? super T> renderer = (OnScreenHintRenderer<? super T>) hint.createRenderer();
        this.registerHint(hint, renderer);
    }

    public <T extends OnScreenHint> void registerHint(T hint, OnScreenHintRenderer<? super T> renderer) {
        HintRenderContainer<T> renderContainer = new HintRenderContainer<>(hint, renderer);
        synchronized (this.hints) {
            this.hints.add(renderContainer);
        }
    }

    public <T extends OnScreenHint & TextHint> void registerActionHint(T hint) {
        OnScreenHintRenderer<? super T> renderer = new LabelOnScreenHint.Renderer<>(LabelOnScreenHint.TextOptions.DEFAULT);
        this.registerActionHint(hint, renderer);
    }

    public <T extends OnScreenHint & TextHint> void registerActionHint(T hint, OnScreenHintRenderer<? super T> customTextRenderer) {
        HintRenderContainer<T> renderContainer = new HintRenderContainer<>(hint, customTextRenderer);
        synchronized (this.actionHints) {
            this.actionHints.add(renderContainer);
        }
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        this.tickHints(client, this.hints);
        this.tickHints(client, this.actionHints);
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, OnScreenHintDisplay displayMode, float delta) {
        Minecraft client = Minecraft.getInstance();
        this.extractDefaultHints(client, graphics, displayMode, delta);
        this.extractCenteredTextHints(client, graphics, displayMode, delta);
    }

    private void tickHints(Minecraft client, Collection<HintRenderContainer<?>> hints) {
        for (HintRenderContainer<?> hintRenderContainer : hints) {
            OnScreenHint hint = hintRenderContainer.instance;
            if (hint.isActive()) {
                hint.tick(client);
            }
        }
    }

    private <T extends OnScreenHint> void extractDefaultHints(Minecraft client, GuiGraphicsExtractor graphics, OnScreenHintDisplay displayMode, float delta) {
        Window window = client.getWindow();
        Font font = client.font;
        int left = 2;
        int top = window.getGuiScaledHeight() - 2;

        for (HintRenderContainer<?> hintRenderContainer : this.hints) {
            HintRenderContainer<T> renderContainer = (HintRenderContainer<T>) hintRenderContainer;
            OnScreenHintRenderer<? super T> renderer = renderContainer.renderer();
            T hint = renderContainer.instance();
            if (hint.shouldRender(displayMode)) {
                int width = renderer.width(hint, window, font);
                int height = renderer.height(hint, window, font);
                top -= height;
                graphics.enableScissor(left, top, left + width, top + height);
                renderer.extract(hint, graphics, window, font, left, top, delta);
                graphics.disableScissor();
            }
        }
    }

    private <T extends OnScreenHint & TextHint> void extractCenteredTextHints(Minecraft client, GuiGraphicsExtractor graphics, OnScreenHintDisplay displayMode, float delta) {
        Window window = client.getWindow();
        Font font = client.font;
        int windowWidth = window.getGuiScaledWidth();
        int top = window.getGuiScaledHeight() / 2 + 10;
        for (HintRenderContainer<?> hintRenderContainer : this.actionHints) {
            HintRenderContainer<T> renderContainer = (HintRenderContainer<T>) hintRenderContainer;
            T hint = renderContainer.instance;
            OnScreenHintRenderer<? super T> renderer = renderContainer.renderer();
            if (hint.shouldRender(displayMode)) {
                int width = renderer.width(hint, window, font);
                int height = renderer.height(hint, window, font);
                int left = (windowWidth - width) / 2;
                graphics.enableScissor(left, top, left + width, top + height);
                renderer.extract(hint, graphics, window, font, left, top, delta);
                graphics.disableScissor();
                top += height;
            }
        }
    }

    private record HintRenderContainer<T extends OnScreenHint>(T instance, OnScreenHintRenderer<? super T> renderer) {}
}
