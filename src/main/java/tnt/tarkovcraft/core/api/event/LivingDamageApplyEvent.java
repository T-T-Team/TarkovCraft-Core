package tnt.tarkovcraft.core.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

public class LivingDamageApplyEvent extends LivingEvent {

    private final DamageContainer container;

    public LivingDamageApplyEvent(LivingEntity entity, DamageContainer container) {
        super(entity);
        this.container = container;
    }

    public DamageSource getDamageSource() {
        return this.container.getSource();
    }

    public float getHealthDamage() {
        return this.container.getNewDamage();
    }

    public float getOriginalDamage() {
        return this.container.getOriginalDamage();
    }

    public float getBlockedDamage() {
        return this.container.getBlockedDamage();
    }

    public float getShieldDamage() {
        return this.container.getShieldDamage();
    }

    public int getPostAttackInvulnerabilityTicks() {
        return this.container.getPostAttackInvulnerabilityTicks();
    }

    public float getReduction(DamageContainer.Reduction type) {
        return this.container.getReduction(type);
    }
}
