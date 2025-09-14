package tnt.tarkovcraft.core.common.attribute;

import net.minecraft.world.entity.LivingEntity;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;
import tnt.tarkovcraft.core.common.weight.WeightSystem;

public class WeightChangeAttributeListener implements AttributeListener {

    private final LivingEntity entity;

    public WeightChangeAttributeListener(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void onAttributeValueChanged(AttributeInstance attribute, double oldValue) {
        WeightSystem.applyWeightEffects(this.entity);
    }

    @Override
    public void onAttributeModifierAdded(AttributeInstance attribute, AttributeModifier modifier) {
        WeightSystem.applyWeightEffects(this.entity);
    }

    @Override
    public void onAttributeModifierRemoved(AttributeInstance attribute, AttributeModifier modifier) {
        WeightSystem.applyWeightEffects(this.entity);
    }
}
