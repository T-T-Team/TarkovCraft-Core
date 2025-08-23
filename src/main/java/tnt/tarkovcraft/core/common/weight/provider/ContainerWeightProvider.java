package tnt.tarkovcraft.core.common.weight.provider;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.weight.WeightContext;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

public class ContainerWeightProvider implements WeightProvider {

    public static final ResourceLocation IDENTIFIER = TarkovCraftCore.createResourceLocation("weight/item/container");

    @Override
    public int getWeight(WeightContext context) {
        ItemStack itemStack = context.itemStack();
        if (!itemStack.has(DataComponents.CONTAINER))
            return 0;
        int weight = 0;
        ItemContainerContents contents = itemStack.get(DataComponents.CONTAINER);
        for (ItemStack contentItemStack : contents.nonEmptyItems()) {
            weight += context.getWeight(contentItemStack);
        }
        return weight;
    }

    @Override
    public WeightSource getSource() {
        return WeightSource.ITEM;
    }
}
