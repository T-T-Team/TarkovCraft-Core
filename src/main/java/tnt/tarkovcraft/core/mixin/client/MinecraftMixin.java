package tnt.tarkovcraft.core.mixin.client;

import com.mojang.blaze3d.platform.WindowEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.extensions.IMinecraftExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.client.shader.PostEffectShaderProgramProcessor;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler, IMinecraftExtension {

    public MinecraftMixin(String name) {
        super(name);
    }

    @Inject(
            method = "setCameraEntity",
            at = @At("RETURN")
    )
    private void tarkovCraftCore$setCameraEntity(@Nullable Entity cameraEntity, CallbackInfo ci) {
        PostEffectShaderProgramProcessor.INSTANCE.resetShaders();
    }
}
