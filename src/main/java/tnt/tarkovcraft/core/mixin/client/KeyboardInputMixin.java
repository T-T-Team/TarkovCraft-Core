package tnt.tarkovcraft.core.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.common.pose.CoreEntityPoseFlags;
import tnt.tarkovcraft.core.common.pose.EntityPoseManager;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tarkovCraftCore$tick(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (EntityPoseManager.isTagged(player, CoreEntityPoseFlags.NO_MOVEMENT)) {
            this.leftImpulse = 0.0F;
            this.forwardImpulse = 0.0F;
            this.up = false;
            this.down = false;
            this.right = false;
            this.left = false;
            this.jumping = false;
            this.shiftKeyDown = false;
            ci.cancel();
        }
    }
}
