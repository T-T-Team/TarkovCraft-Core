package tnt.tarkovcraft.core.api.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public interface PostEffectShaderProgram {

    ResourceLocation postChainId();

    void tickProgram(Minecraft client, LivingEntity cameraEntity);

    boolean active();

    void onRender(float delta, UniformSetter uniformSetter);

    @FunctionalInterface
    interface UniformSetter {
        void setUniform(String uniformName, float value);
    }
}
