package tnt.tarkovcraft.core.api;

import net.minecraft.world.entity.LivingEntity;

public interface MovementStaminaComponent {

    boolean isAttached(LivingEntity entity);

    void update(LivingEntity entity);

    void setStamina(LivingEntity entity, int amount);

    boolean canSprint(LivingEntity entity);

    boolean canJump(LivingEntity entity);

    void onJump(LivingEntity entity);
}
