package tnt.tarkovcraft.core.common.attribute;

import net.minecraft.server.level.ServerPlayer;
import tnt.tarkovcraft.core.common.attribute.modifier.AttributeModifier;

public class SynchronizationAttributeListener implements AttributeListener {

    private final ServerPlayer holder;

    public SynchronizationAttributeListener(ServerPlayer holder) {
        this.holder = holder;
    }

    @Override
    public void onAttributeValueChanged(AttributeInstance attribute, double oldValue) {
        this.synchronize();
    }

    @Override
    public void onAttributeModifierAdded(AttributeInstance attribute, AttributeModifier modifier) {
        this.synchronize();
    }

    @Override
    public void onAttributeModifierRemoved(AttributeInstance attribute, AttributeModifier modifier) {
        this.synchronize();
    }

    @SuppressWarnings("ConstantValue") // IDEA, get your shit together
    public void synchronize() {
        if (this.holder != null && this.holder.connection != null) {
            AttributeSystem.sync(this.holder);
        }
    }
}
