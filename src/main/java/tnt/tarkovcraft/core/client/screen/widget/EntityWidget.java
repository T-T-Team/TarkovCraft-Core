package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

public class EntityWidget extends AbstractWidget {

    private final LivingEntity entity;
    private float yOffset;
    private int background;

    public EntityWidget(int x, int y, int width, int height, LivingEntity entity) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.entity = entity;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public void setOffset(float y) {
        this.yOffset = y;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
        if (RenderUtils.isVisibleColor(this.background)) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.background);
        }
        InventoryScreen.extractEntityInInventoryFollowsMouse(
                guiGraphics,
                this.getX(), this.getY(), this.getRight(), this.getBottom(),
                Mth.floor(Math.min(this.width, this.height) / 3.0F),
                this.yOffset,
                mouseX, mouseY,
                this.entity
        );
        guiGraphics.disableScissor();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }
}
