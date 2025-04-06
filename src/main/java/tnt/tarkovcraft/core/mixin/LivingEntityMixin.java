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
import tnt.tarkovcraft.core.common.attribute.AttributeInstance;
import tnt.tarkovcraft.core.common.attribute.EntityAttributeData;
import tnt.tarkovcraft.core.common.energy.EnergyData;
import tnt.tarkovcraft.core.common.energy.EnergyType;
import tnt.tarkovcraft.core.common.init.BaseAttributes;
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
        if (hasData(BaseDataAttachments.ENTITY_ATTRIBUTES)) {
            EntityAttributeData entityAttributeData = getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
            AttributeInstance instance = entityAttributeData.getAttribute(BaseAttributes.SPRINT);
            if (!instance.booleanValue()) {
                ci.cancel();
            }
        }
        if (hasData(BaseDataAttachments.ENERGY)) {
            EnergyData energyData = getData(BaseDataAttachments.ENERGY);
            if (!energyData.hasEnergy(EnergyType.LEG_STAMINA, EnergyData.SPRINT_STAMINA_CONSUMPTION)) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;tickEffects()V")
    )
    private void tarkovCraftCore$tick(CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        if (hasData(BaseDataAttachments.ENTITY_ATTRIBUTES)) {
            getData(BaseDataAttachments.ENTITY_ATTRIBUTES).tick();
        }
        if (hasData(BaseDataAttachments.ENERGY)) {
            getData(BaseDataAttachments.ENERGY).update(livingEntity);
        }
    }
}
