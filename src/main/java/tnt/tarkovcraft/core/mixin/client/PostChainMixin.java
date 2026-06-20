package tnt.tarkovcraft.core.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.PostChain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tnt.tarkovcraft.core.client.shader.PostEffectShaderProgramProcessor;

@Mixin(PostChain.class)
public abstract class PostChainMixin {

    @ModifyExpressionValue(
            method = "createPass",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderPipeline$Builder;build()Lcom/mojang/blaze3d/pipeline/RenderPipeline;")
    )
    private static RenderPipeline tarkovCraftCore$buildPostChainPipeline(RenderPipeline pipeline) {
        return PostEffectShaderProgramProcessor.INSTANCE.modifyDynamicPipeline(pipeline);
    }
}
