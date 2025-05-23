package tnt.tarkovcraft.core.common.data.filter;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface ItemStackFilter extends Predicate<ItemStack> {

    ItemStackFilterType<?> getType();
}
