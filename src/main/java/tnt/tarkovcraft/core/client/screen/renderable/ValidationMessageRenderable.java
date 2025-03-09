package tnt.tarkovcraft.core.client.screen.renderable;

import dev.toma.configuration.config.validate.IValidationResult;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Supplier;

public class ValidationMessageRenderable extends AbstractRenderable {

    private final boolean onlyWarnOrError;
    private final Font font;
    private final Supplier<IValidationResult> resultProvider;

    public ValidationMessageRenderable(int x, int y, int width, int height, boolean onlyWarnOrError, Font font, Supplier<IValidationResult> resultProvider) {
        super(x, y, width, height);
        this.onlyWarnOrError = onlyWarnOrError;
        this.font = font;
        this.resultProvider = resultProvider;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        IValidationResult result = this.resultProvider.get();
        IValidationResult.Severity severity = result.severity();
        if (!this.onlyWarnOrError || severity.isWarningOrError()) {
            guiGraphics.blit(RenderType::guiTextured, severity.iconPath, this.x, this.y, 0.0F, 0.0F, 10, 10, 16, 16, 16, 16);
            List<Component> messages = result.messages();
            if (this.height >= 10 && !messages.isEmpty()) {
                int renderCount = Math.min(this.height / 10, messages.size());
                for (int i = 0; i < renderCount; i++) {
                    Component message = messages.get(i);
                    MutableComponent text = message.copy().withStyle(ChatFormatting.ITALIC);
                    guiGraphics.drawScrollingString(this.font, text, this.x + 12, this.getRight(), this.y + 2 + i * 10, severity.textColor);
                }
            }
        }
    }
}
