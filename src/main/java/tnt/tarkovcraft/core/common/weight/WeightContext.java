package tnt.tarkovcraft.core.common.weight;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.function.ToIntFunction;

public record WeightContext(WeightProvider.WeightSource source, int baseWeight, ItemInstance item, @Nullable LivingEntity entity, ToIntFunction<ItemInstance> itemWeight) {

    public static WeightContext itemStack(int weight, ItemInstance instance, ToIntFunction<ItemInstance> itemWeight) {
        return new WeightContext(WeightProvider.WeightSource.ITEM, weight, instance, null, itemWeight);
    }

    public static WeightContext entity(LivingEntity entity, ToIntFunction<ItemInstance> itemWeight) {
        return new WeightContext(WeightProvider.WeightSource.ENTITY, 0, ItemStack.EMPTY, entity, itemWeight);
    }

    public boolean isItem() {
        return this.source.isItem();
    }

    public boolean isEntity() {
        return this.source.isEntity();
    }

    public int getWeight(ItemInstance instance) {
        return this.itemWeight.applyAsInt(instance);
    }
}
