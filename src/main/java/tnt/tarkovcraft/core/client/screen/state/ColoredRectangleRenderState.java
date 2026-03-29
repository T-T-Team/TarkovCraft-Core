package tnt.tarkovcraft.core.client.screen.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.jspecify.annotations.Nullable;
import org.joml.Matrix3x2f;

public record ColoredRectangleRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        float x1, float y1,
        float x2, float y2,
        int primaryColor, int secondaryColor,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {

    @Override
    public void buildVertices(VertexConsumer consumer) {
        consumer.addVertexWith2DPose(this.pose, this.x1, this.y1).setColor(this.primaryColor);
        consumer.addVertexWith2DPose(this.pose, this.x1, this.y2).setColor(this.secondaryColor);
        consumer.addVertexWith2DPose(this.pose, this.x2, this.y2).setColor(this.secondaryColor);
        consumer.addVertexWith2DPose(this.pose, this.x2, this.y1).setColor(this.primaryColor);
    }
}
