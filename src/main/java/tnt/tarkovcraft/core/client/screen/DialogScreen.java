package tnt.tarkovcraft.core.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.lwjgl.glfw.GLFW;
import tnt.tarkovcraft.core.client.screen.renderable.AbstractTextRenderable;
import tnt.tarkovcraft.core.client.screen.renderable.HorizontalLineRenderable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class DialogScreen extends OverlayScreen {

    protected final List<Component> body;
    protected final DialogResponder onConfirm;
    protected final DialogResponder onCancel;
    protected final int windowWidth;
    protected final int windowHeight;
    protected final Integer windowBackground;
    protected final int windowColor;
    protected final Integer frameSize;
    protected final int frameColor;
    protected final Function<DialogScreen, AbstractButton> cancelButtonProvider;
    protected final Function<DialogScreen, AbstractButton> confirmButtonProvider;

    protected int left, top;

    protected DialogScreen(Builder builder) {
        super(Objects.requireNonNull(builder.title), Objects.requireNonNull(builder.parent));
        this.body = builder.body;
        this.onConfirm = Objects.requireNonNull(builder.onConfirm);
        this.onCancel = Objects.requireNonNull(builder.onCancel);
        this.windowWidth = builder.width;
        this.windowHeight = builder.height;
        this.windowBackground = builder.backgroundColor;
        this.windowColor = builder.windowColor;
        this.frameSize = builder.frameSize;
        this.frameColor = builder.frameColor;
        this.cancelButtonProvider = Objects.requireNonNull(builder.cancelButtonProvider);
        this.confirmButtonProvider = Objects.requireNonNull(builder.confirmButtonProvider);
    }

    public static DialogScreen.Builder builder(Component title, Screen parent) {
        return new Builder(parent, title);
    }

    public final void openDialog() {
        if (this.minecraft != null) {
            throw new IllegalStateException("Cannot reopen already initialized dialog");
        }
        Minecraft.getInstance().setScreen(this);
    }

    @Override
    protected void init() {
        super.init();
        this.left = (this.width - this.windowWidth) / 2;
        this.top = (this.height - this.windowHeight) / 2;
        this.addTitle();
        this.addBody();
        this.addTitleSeparator();
        this.addControlButtons();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderBlurredBackground();
        if (this.windowBackground != null) {
            guiGraphics.fill(0, 0, this.width, this.height, this.windowBackground);
        }
        if (this.frameSize != null) {
            guiGraphics.fill(this.left - this.frameSize, this.top - this.frameSize, this.left + this.windowWidth + this.frameSize, this.top + this.windowHeight + this.frameSize, this.frameColor);
        }
        guiGraphics.fill(this.left, this.top, this.left + this.windowWidth, this.top + this.windowHeight, this.windowColor);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.allowKeyboardInput()) {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                this.onConfirm.onEvent(this);
                return true;
            } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.onCancel.onEvent(this);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected void addTitle() {
        int margin = this.getButtonMargin();
        this.addRenderableOnly(new AbstractTextRenderable.Component(this.left + margin, this.top + 2, this.windowWidth - 2 * margin, 10, 0xFFFFFF, false, this.font, this.title));
    }

    protected void addBody() {
        int margin = this.getButtonMargin();
        List<FormattedCharSequence> formattedText = this.body.stream()
                .flatMap(comp -> this.font.split(comp, this.windowWidth - margin * 2).stream())
                .toList();
        int index = 0;
        for (FormattedCharSequence text : formattedText) {
            this.addRenderableOnly(new AbstractTextRenderable.FormattedSequence(this.left + margin, this.top + 15 + index++ * 10, this.windowWidth - 2 * margin, 10, 0xFFFFFF, false, this.font, text));
        }
    }

    protected void addTitleSeparator() {
        int margin = this.getButtonMargin();
        if (this.frameSize != null)
            this.addRenderableOnly(new HorizontalLineRenderable(this.left + margin - 1, this.windowWidth - 2 * margin + 1, this.top + 13, this.frameColor));
    }

    protected void addControlButtons() {
        int margin = this.getButtonMargin();
        int buttonLeft = this.left + margin;
        int buttonWidth = (this.windowWidth - 3 * margin) / 2;
        AbstractButton cancelButton = this.addRenderableWidget(this.cancelButtonProvider.apply(this));
        cancelButton.setX(buttonLeft);
        cancelButton.setY(this.top + this.windowHeight - 20);
        cancelButton.setWidth(buttonWidth);
        cancelButton.setHeight(15);
        AbstractButton confirmButton = this.addRenderableWidget(this.confirmButtonProvider.apply(this));
        confirmButton.setX(buttonLeft + this.getButtonMargin() + buttonWidth);
        confirmButton.setY(this.top + this.windowHeight - 20);
        confirmButton.setWidth(buttonWidth);
        confirmButton.setHeight(15);
    }

    public void handleConfirmed() {
        this.onConfirm.onEvent(this);
    }

    public void handleCancelled() {
        this.onCancel.onEvent(this);
    }

    protected int getButtonMargin() {
        return 5;
    }

    protected boolean allowKeyboardInput() {
        return true;
    }

    @FunctionalInterface
    public interface DialogResponder {
        void onEvent(DialogScreen screen);
    }

    public static class Builder {

        private final Screen parent;
        private final Component title;
        private final List<Component> body = new ArrayList<>();
        private DialogResponder onConfirm;
        private DialogResponder onCancel = LayeredScreen::openParentScreen;
        private int width = 176, height = 140;
        private Integer backgroundColor = ColorPalette.BG_TRANSPARENT_WEAK;
        private int windowColor = 0xFF << 24;
        private Integer frameSize = 1;
        private int frameColor = 0xFFFFFFFF;
        private Function<DialogScreen, AbstractButton> cancelButtonProvider = dialog -> Button.builder(CommonComponents.GUI_NO, b -> dialog.handleCancelled()).build();
        private Function<DialogScreen, AbstractButton> confirmButtonProvider = dialog -> Button.builder(CommonComponents.GUI_YES, b -> dialog.handleConfirmed()).build();

        protected Builder(Screen parent, Component title) {
            this.parent = parent;
            this.title = title;
        }

        public Builder addMessage(Component message) {
            this.body.add(message);
            return this;
        }

        public Builder onConfirm(DialogResponder onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onCancel(DialogResponder onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder dimensions(int width, int height) {
            return this.width(width).height(height);
        }

        public Builder backgroundColor(Integer backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder windowColor(int windowColor) {
            this.windowColor = windowColor;
            return this;
        }

        public Builder frame(Integer size, int color) {
            this.frameSize = size;
            this.frameColor = color;
            return this;
        }

        public Builder cancelButton(Function<DialogScreen, AbstractButton> cancelButtonProvider) {
            this.cancelButtonProvider = cancelButtonProvider;
            return this;
        }

        public Builder confirmButton(Function<DialogScreen, AbstractButton> confirmButtonProvider) {
            this.confirmButtonProvider = confirmButtonProvider;
            return this;
        }

        public DialogScreen build() {
            return build(DialogScreen::new);
        }

        public DialogScreen buildAndOpen() {
            return buildAndOpen(DialogScreen::new);
        }

        public <D extends DialogScreen> D build(Function<Builder, D> builder) {
            return builder.apply(this);
        }

        public <D extends DialogScreen> D buildAndOpen(Function<Builder, D> builder) {
            D dialog = builder.apply(this);
            dialog.openDialog();
            return dialog;
        }
    }
}
