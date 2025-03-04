package tnt.tarkovcraft.core.common.init.filter.itemstack;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface ItemStackFilter extends Predicate<ItemStack> {

    ItemStackFilterType<?> getType();
}
