package tnt.tarkovcraft.core.common.weight.provider;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.weight.WeightContext;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

public class BundleWeightProvider implements WeightProvider {

    public static final Identifier IDENTIFIER = TarkovCraftCore.createIdentifier("weight/item/bundle");

    @Override
    public int getWeight(WeightContext context) {
        ItemInstance instance = context.item();
        if (!instance.has(DataComponents.BUNDLE_CONTENTS))
            return 0;
        BundleContents contents = instance.get(DataComponents.BUNDLE_CONTENTS);
        int weight = 0;
        for (ItemStackTemplate bundleItemStack : contents.items()) {
            weight += context.getWeight(bundleItemStack);
        }
        return weight;
    }

    @Override
    public WeightSource getSource() {
        return WeightSource.ITEM;
    }
}
