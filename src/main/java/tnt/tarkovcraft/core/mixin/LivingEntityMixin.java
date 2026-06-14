package tnt.tarkovcraft.core.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.common.extensions.ILivingEntityExtension;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tnt.tarkovcraft.core.api.MovementStaminaComponent;
import tnt.tarkovcraft.core.api.event.CoreEventHooks;
import tnt.tarkovcraft.core.common.energy.EnergySystem;
import tnt.tarkovcraft.core.common.pose.CoreEntityPoseFlags;
import tnt.tarkovcraft.core.common.pose.EntityPoseManager;

import java.util.Stack;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, ILivingEntityExtension {

    @Shadow
    @Nullable
    protected Stack<DamageContainer> damageContainers;

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
        MovementStaminaComponent component = EnergySystem.MOVEMENT_STAMINA.getComponent();
        if (!component.canSprint(entity)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "jumpFromGround",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;"),
            cancellable = true
    )
    private void tarkovCraftCore$jumpFromGround(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        MovementStaminaComponent component = EnergySystem.MOVEMENT_STAMINA.getComponent();
        if (component.canJump(entity)) {
            component.onJump(entity);
        } else {
            ci.cancel();
        }
    }

    @Inject(
            method = "swing(Lnet/minecraft/world/InteractionHand;Z)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tarkovCraftCore$swing(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (EntityPoseManager.isTagged(livingEntity, CoreEntityPoseFlags.NO_INTERACTION)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "isPushable",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tarkovCraftCore$isPushable(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (EntityPoseManager.isTagged(livingEntity, CoreEntityPoseFlags.NO_KNOCKBACK)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;gameEvent(Lnet/minecraft/core/Holder;)V", shift = At.Shift.AFTER)
    )
    private void tarkovCraftCore$actuallyHurt(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        CoreEventHooks.onLivingApplyDamage(entity, damageContainers);
    }
}
