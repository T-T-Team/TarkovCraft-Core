package tnt.tarkovcraft.core.mixin;

import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tnt.tarkovcraft.core.common.energy.EnergySystem;
import tnt.tarkovcraft.core.common.energy.MovementStamina;
import tnt.tarkovcraft.core.common.init.BaseDataAttachments;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, ILivingEntityExtension {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "setSprinting",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tarkovCraftCore$setSprinting(boolean sprinting, CallbackInfo ci) {
        if (!sprinting) {
            return; // we do not care when entity is transitioning from sprinting state
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        if (EnergySystem.isEnabled() && hasData(BaseDataAttachments.STAMINA)) {
            MovementStamina movementStamina = getData(BaseDataAttachments.STAMINA);
            if (!movementStamina.canSprint(entity)) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "jumpFromGround",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"),
            cancellable = true
    )
    private void tarkovCraftCore$jumpFromGround(CallbackInfo ci) {
        if (EnergySystem.isEnabled() && hasData(BaseDataAttachments.STAMINA)) {
            LivingEntity livingEntity = (LivingEntity) (Object) this;
            MovementStamina movementStamina = getData(BaseDataAttachments.STAMINA);
            if (!movementStamina.canJump(livingEntity)) {
                ci.cancel();
            } else {
                movementStamina.onJump(livingEntity);
            }
        }
    }
}
