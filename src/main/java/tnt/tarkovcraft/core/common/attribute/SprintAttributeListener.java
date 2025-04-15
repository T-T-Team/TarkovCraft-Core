package tnt.tarkovcraft.core.common.attribute;

import net.minecraft.world.entity.Entity;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class SprintAttributeListener implements AttributeListener {

    private final BooleanSupplier sprintAllowed;
    private final Supplier<Entity> holder;

    public SprintAttributeListener(BooleanSupplier sprintAllowed, Supplier<Entity> holder) {
        this.sprintAllowed = sprintAllowed;
        this.holder = holder;
    }

    @Override
    public void onAttributeModifierAdded(AttributeInstance attribute, AttributeModifier modifier) {
        boolean canSprint = this.sprintAllowed.getAsBoolean();
        if (!canSprint) {
            Entity entity = this.holder.get();
            if (entity != null)
                entity.setSprinting(false);
        }
    }
}
