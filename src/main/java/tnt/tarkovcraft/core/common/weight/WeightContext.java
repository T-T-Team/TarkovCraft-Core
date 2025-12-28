package tnt.tarkovcraft.core.common.weight;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jspecify.annotations.Nullable;
import java.util.function.ToIntFunction;

public record WeightContext(WeightProvider.WeightSource source, int baseWeight, ItemStack itemStack, @Nullable LivingEntity entity, ToIntFunction<ItemStack> itemWeight) {

    public static WeightContext itemStack(int weight, ItemStack itemStack, ToIntFunction<ItemStack> itemWeight) {
        return new WeightContext(WeightProvider.WeightSource.ITEM, weight, itemStack, null, itemWeight);
    }

    public static WeightContext entity(LivingEntity entity, ToIntFunction<ItemStack> itemWeight) {
        return new WeightContext(WeightProvider.WeightSource.ENTITY, 0, ItemStack.EMPTY, entity, itemWeight);
    }

    public boolean isItem() {
        return this.source.isItem();
    }

    public boolean isEntity() {
        return this.source.isEntity();
    }

    public int getWeight(ItemStack itemStack) {
        return this.itemWeight.applyAsInt(itemStack);
    }
}
