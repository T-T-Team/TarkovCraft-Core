package tnt.tarkovcraft.core.common.attribute;

import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;

public interface AttributeListener {

    default void onAttributeSetChanged(Attribute attribute) {}

    default void onAttributeValueChanged(Attribute attribute, double oldValue) {}

    default void onAttributeModifierAdded(Attribute attribute, AttributeModifier modifier) {}

    default void onAttributeModifierRemoved(Attribute attribute, AttributeModifier modifier) {}

    default void onAttributeAdded(Attribute attribute) {}

    default void onAttributeRemoved(Attribute attribute) {}
}
