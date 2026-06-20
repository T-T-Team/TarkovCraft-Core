package tnt.tarkovcraft.core.api.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public interface PostEffectShaderProgram {

    void tickProgram(Minecraft client, LivingEntity cameraEntity);

    void onRender(DeltaTracker tracker);

    boolean active();

    Identifier postChainId();

    ShaderType getShaderType();

    @Nullable default GpuBufferSlice getDynamicUniformBuffer() {
        return null;
    }

    default void applyDynamicUniforms(Consumer<Identifier> passIdentifierConsumer) {
    }
}
