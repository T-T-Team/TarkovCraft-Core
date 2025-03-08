package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ContainerWidget extends AbstractWidget implements ContainerEventHandler {

    private final List<Renderable> renderables;
    private final List<GuiEventListener> children;
    private final List<NarratableEntry> narratables;
    private boolean isDragging;
    private GuiEventListener focused;

    public ContainerWidget(int x, int y, int width, int height, Component title) {
        super(x, y, width, height, title);
        this.renderables = new ArrayList<>();
        this.children = new ArrayList<>();
        this.narratables = new ArrayList<>();
    }

    public <T extends Renderable> T addRenderableOnly(T renderable) {
        this.renderables.add(renderable);
        return renderable;
    }

    public <T extends GuiEventListener & NarratableEntry> T addWidget(T listener) {
        this.children.add(listener);
        this.narratables.add(listener);
        return listener;
    }

    public <T extends GuiEventListener & NarratableEntry & Renderable> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        return this.addWidget(widget);
    }

    public void removeWidget(GuiEventListener listener) {
        if (listener instanceof Renderable) {
            this.renderables.remove(listener);
        }
        if (listener instanceof NarratableEntry) {
            this.narratables.remove(listener);
        }
        this.children.remove(listener);
    }

    public void clearWidgets() {
        this.renderables.clear();
        this.children.clear();
        this.narratables.clear();
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        ContainerEventHandler.super.setFocused(focused);
    }

    @Override
    public final boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public final void setDragging(boolean p_313698_) {
        this.isDragging = p_313698_;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener eventListener) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (eventListener != null) {
            eventListener.setFocused(true);
        }

        this.focused = eventListener;
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent navigationEvent) {
        return ContainerEventHandler.super.nextFocusPath(navigationEvent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        return ContainerEventHandler.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return ContainerEventHandler.super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
