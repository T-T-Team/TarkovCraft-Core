package tnt.tarkovcraft.core.mixin.client;

import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.client.shader.PostEffectShaderProgramProcessor;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private CrossFrameResourcePool resourcePool;

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = At.Shift.AFTER)
    )
    private void tarkovCraftCore$render(CallbackInfo ci) {
        PostEffectShaderProgramProcessor.INSTANCE.render(this.minecraft, this.resourcePool);
    }
}
