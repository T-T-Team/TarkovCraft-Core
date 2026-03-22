package tnt.tarkovcraft.core.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.client.shader.PostEffectShaderProgramProcessor;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V", shift = At.Shift.AFTER)
    )
    private void medsystem$render(DeltaTracker tracker, boolean renderLevel, CallbackInfo ci) {
        PostEffectShaderProgramProcessor.INSTANCE.render(tracker.getGameTimeDeltaTicks());
    }

    @Inject(
            method = "resize",
            at = @At("HEAD")
    )
    private void medsystem$resize(int width, int height, CallbackInfo ci) {
        PostEffectShaderProgramProcessor.INSTANCE.resize(width, height);
    }
}
