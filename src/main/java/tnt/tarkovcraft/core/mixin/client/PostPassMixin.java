package tnt.tarkovcraft.core.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.client.shader.PostEffectShaderProgramProcessor;

@Mixin(PostPass.class)
public abstract class PostPassMixin {

    @Inject(
            method = "lambda$addToFrame$1",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindDefaultUniforms(Lcom/mojang/blaze3d/systems/RenderPass;)V")
    )
    private void tarkovCraftCore$lambda$addToFrame$5(CallbackInfo ci, @Local RenderPass pass) {
        GpuBufferSlice slice = PostEffectShaderProgramProcessor.INSTANCE.getActiveDynamicUniformBuffer();
        if (slice != null) {
            pass.setUniform("DynamicTransforms", slice);
        }
    }
}
