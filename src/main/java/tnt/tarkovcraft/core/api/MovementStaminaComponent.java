package tnt.tarkovcraft.core.api;

import net.minecraft.world.entity.LivingEntity;

public interface MovementStaminaComponent extends StaminaComponent {

    boolean canSprint(LivingEntity entity);

    boolean canJump(LivingEntity entity);

    void onJump(LivingEntity entity);
}
