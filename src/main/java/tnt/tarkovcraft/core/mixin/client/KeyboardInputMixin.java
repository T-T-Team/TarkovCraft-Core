package tnt.tarkovcraft.core.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.common.pose.CoreEntityPoseFlags;
import tnt.tarkovcraft.core.common.pose.EntityPoseManager;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends ClientInput {

    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tarkovCraftCore$tick(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (EntityPoseManager.isTagged(player, CoreEntityPoseFlags.NO_MOVEMENT)) {
            this.moveVector = Vec2.ZERO;
            this.keyPresses = new Input(false, false, false, false, false, false, false);
            ci.cancel();
        }
    }
}
