package tnt.tarkovcraft.core.common.weight.provider;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.weight.WeightContext;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

public class BundleWeightProvider implements WeightProvider {

    public static final ResourceLocation IDENTIFIER = TarkovCraftCore.createResourceLocation("weight/item/bundle");

    @Override
    public int getWeight(WeightContext context) {
        ItemStack itemStack = context.itemStack();
        if (!itemStack.has(DataComponents.BUNDLE_CONTENTS))
            return 0;
        BundleContents contents = itemStack.get(DataComponents.BUNDLE_CONTENTS);
        int weight = 0;
        for (ItemStack bundleItemStack : contents.items()) {
            weight += context.getWeight(bundleItemStack);
        }
        return weight;
    }

    @Override
    public WeightSource getSource() {
        return WeightSource.ITEM;
    }
}
