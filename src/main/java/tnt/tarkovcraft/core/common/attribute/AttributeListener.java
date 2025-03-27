package tnt.tarkovcraft.core.common.attribute;

import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;

public interface AttributeListener {

    default void onAttributeSetChanged(AttributeInstance attribute) {}

    default void onAttributeValueChanged(AttributeInstance attribute, double oldValue) {}

    default void onAttributeModifierAdded(AttributeInstance attribute, AttributeModifier modifier) {}

    default void onAttributeModifierRemoved(AttributeInstance attribute, AttributeModifier modifier) {}
}
