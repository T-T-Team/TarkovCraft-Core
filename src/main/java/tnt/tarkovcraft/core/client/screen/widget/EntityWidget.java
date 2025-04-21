package tnt.tarkovcraft.core.client.screen.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tnt.tarkovcraft.core.util.helper.RenderUtils;

public class EntityWidget extends AbstractWidget {

    private final LivingEntity entity;
    private Vector3f offset;
    private Quaternionf pose;
    private Quaternionf camera;
    private int background;

    public EntityWidget(int x, int y, int width, int height, LivingEntity entity) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.entity = entity;
        this.offset = new Vector3f();
        this.pose = new Quaternionf().rotateZ((float) Math.PI);
        this.camera = new Quaternionf().rotateX((float) Math.toRadians(-15.0F));
        this.pose.mul(this.camera);
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    public void setOffset(float x, float y, float z) {
        this.offset = new Vector3f(x, y, z);
    }

    public void setPose(Quaternionf pose) {
        this.pose = pose;
    }

    public void setCamera(Quaternionf camera) {
        this.camera = camera;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
        if (RenderUtils.isVisibleColor(this.background)) {
            guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), this.background);
        }
        float yBodyRot = this.entity.yBodyRot;
        float yRot = this.entity.getYRot();
        float xRot = this.entity.getXRot();
        float yHeadRotO = this.entity.yHeadRotO;
        float yHeadRot = this.entity.yHeadRot;
        this.entity.setYRot(0.0F);
        this.entity.setXRot(-10.0F);
        this.entity.setYBodyRot(160.0F);
        this.entity.setYHeadRot(180.0F);
        InventoryScreen.renderEntityInInventory(
                guiGraphics,
                this.getX() + this.getWidth() / 2.0F,
                this.getY() + this.getHeight() - this.getHeight() / 4.0F,
                Math.min(this.width, this.height) / 3.0F,
                this.offset,
                this.pose,
                this.camera,
                this.entity
        );
        this.entity.yBodyRot = yBodyRot;
        this.entity.setYRot(yRot);
        this.entity.setXRot(xRot);
        this.entity.yHeadRotO = yHeadRotO;
        this.entity.yHeadRot = yHeadRot;
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
