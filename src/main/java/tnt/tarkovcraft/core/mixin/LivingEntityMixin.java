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
        if (hasData(BaseDataAttachments.ENTITY_ATTRIBUTES)) {
            EntityAttributeData entityAttributeData = getData(BaseDataAttachments.ENTITY_ATTRIBUTES);
            AttributeInstance instance = entityAttributeData.getAttribute(BaseAttributes.SPRINT);
            if (sprinting && !instance.booleanValue()) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;tickEffects()V")
    )
    private void tarkovCraftCore$tick(CallbackInfo ci) {
        if (hasData(BaseDataAttachments.ENTITY_ATTRIBUTES)) {
            getData(BaseDataAttachments.ENTITY_ATTRIBUTES).tick();
        }
    }
}
