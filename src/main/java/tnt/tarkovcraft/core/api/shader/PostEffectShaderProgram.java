package tnt.tarkovcraft.core.api.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

public interface PostEffectShaderProgram {

    void tickProgram(Minecraft client, LivingEntity cameraEntity);

    boolean active();

    Identifier postChainId();

    @Nullable default GpuBufferSlice getDynamicUniformBuffer() {
        return null;
    }
}
