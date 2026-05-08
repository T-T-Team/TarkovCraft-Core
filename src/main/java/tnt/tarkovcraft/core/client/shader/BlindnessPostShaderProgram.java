package tnt.tarkovcraft.core.client.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.api.shader.PostEffectShaderProgram;
import tnt.tarkovcraft.core.api.shader.ShaderType;
import tnt.tarkovcraft.core.common.attribute.AttributeSystem;
import tnt.tarkovcraft.core.common.init.CoreAttributes;

public final class BlindnessPostShaderProgram implements PostEffectShaderProgram {

    public static final BlindnessPostShaderProgram INSTANCE = new BlindnessPostShaderProgram();
    public static final ResourceLocation IDENTIFIER = TarkovCraftCore.createResourceLocation("blindness");

    private float strength = 1.0F;
    private float lastStrength = 1.0F;

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
    public void onRender(float delta, UniformSetter setter) {
        float f = Mth.lerp(delta, this.lastStrength, this.strength);
        float easedScale = f * f * f;
        setter.setUniform("Scale", easedScale);
    }

    @Override
    public boolean active() {
        return this.strength < 1.0F;
    }

    @Override
    public ResourceLocation postChainId() {
        return IDENTIFIER;
    }

    @Override
    public ShaderType getShaderType() {
        return ShaderType.GAME;
    }
}
