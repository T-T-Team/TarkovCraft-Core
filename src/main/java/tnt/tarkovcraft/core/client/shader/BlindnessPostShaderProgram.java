package tnt.tarkovcraft.core.client.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.EasingType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;
import tnt.tarkovcraft.core.api.shader.ShaderType;
import tnt.tarkovcraft.core.common.attribute.AttributeSystem;
import tnt.tarkovcraft.core.common.init.CoreAttributes;

// TODO light effects to support flash grenades?
public final class BlindnessPostShaderProgram implements PostEffectShaderProgram {

    public static final BlindnessPostShaderProgram INSTANCE = new BlindnessPostShaderProgram();
    public static final Identifier IDENTIFIER = TarkovCraftCore.createIdentifier("blindness");
    public static final Identifier DYNAMIC_PIPELINE = ShaderHelper.getPostChainPipeline(IDENTIFIER, 0);

    private float strength = 1.0F;
    private float lastStrength = 1.0F;
    private float interpolatedStrength = 1.0F;

    private BlindnessPostShaderProgram() {}

    @Override
    public void tickProgram(Minecraft client, LivingEntity cameraEntity) {
        this.lastStrength = this.strength;
        float vision = Mth.clamp(AttributeSystem.getFloatValue(cameraEntity, CoreAttributes.VISION, 1.0F), 0.0F, 1.0F);
        if (vision < this.strength) {
            this.strength = vision;
        } else {
            this.strength = Math.min(this.strength + 0.001F, vision);
        }
    }

    @Override
    public void onRender(DeltaTracker tracker) {
        this.interpolatedStrength = EasingType.IN_CUBIC.apply(Mth.lerp(tracker.getGameTimeDeltaTicks(), this.lastStrength, this.strength));
    }

    @Override
    public boolean active() {
        return this.strength < 1.0F;
    }

    @Override
    public Identifier postChainId() {
        return IDENTIFIER;
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.GAME;
    }

    @Override
    public @Nullable GpuBufferSlice getDynamicUniformBuffer() {
        return ShaderHelper.scaleTransform(1.0F - this.interpolatedStrength);
    }
}
