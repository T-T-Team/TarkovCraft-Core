package tnt.tarkovcraft.core.common.weight.provider;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.item.Currency;
import tnt.tarkovcraft.core.common.weight.WeightContext;
import tnt.tarkovcraft.core.common.weight.WeightProvider;

public class CurrencyWeightProvider implements WeightProvider {

    public static final Identifier IDENTIFIER = TarkovCraftCore.createIdentifier("weight/item/currency");

    @Override
    public WeightSource getSource() {
        return WeightSource.ITEM;
    }

    @Override
    public int getWeight(WeightContext weightContext) {
        ItemStack itemStack = weightContext.itemStack();
        Currency currency = Currency.getFromItemStack(itemStack);
        if (currency == null)
            return 0;
        int baseWeight = weightContext.baseWeight();
        if (baseWeight == 0)
            return 0;
        int amount = currency.amount();
        int stacks = Math.max(amount / 50 - 1, 0);
        return stacks * baseWeight;
    }
}
