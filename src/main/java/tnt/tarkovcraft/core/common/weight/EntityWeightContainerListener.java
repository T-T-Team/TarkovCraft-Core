package tnt.tarkovcraft.core.common.weight;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.TarkovCraftCore;

import java.util.Objects;

public final class EntityWeightContainerListener implements ContainerListener {

    public static final Identifier IDENTIFIER = TarkovCraftCore.createIdentifier("inventory_listener/weight");
    private final LivingEntity entity;

    public EntityWeightContainerListener(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
        WeightSystem.applyWeightEffects(this.entity);
    }

    @Override
    public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EntityWeightContainerListener listener && listener.entity.getId() == this.entity.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(IDENTIFIER);
    }

}
