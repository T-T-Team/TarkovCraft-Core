package tnt.tarkovcraft.core.common.weight.provider;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.component.ItemContainerContents;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.weight.WeightContext;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

public class ContainerWeightProvider implements WeightProvider {

    public static final Identifier IDENTIFIER = TarkovCraftCore.createIdentifier("weight/item/container");

    @Override
    public int getWeight(WeightContext context) {
        ItemInstance instance = context.item();
        if (!instance.has(DataComponents.CONTAINER))
            return 0;
        int weight = 0;
        ItemContainerContents contents = instance.get(DataComponents.CONTAINER);
        for (ItemInstance contentItemStack : contents.nonEmptyItems()) {
            weight += context.getWeight(contentItemStack);
        }
        return weight;
    }

    @Override
    public WeightSource getSource() {
        return WeightSource.ITEM;
    }
}
