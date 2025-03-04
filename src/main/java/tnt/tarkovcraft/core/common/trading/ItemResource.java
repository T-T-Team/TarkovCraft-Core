package tnt.tarkovcraft.core.common.trading;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.TarkovCraftCore;
import tnt.tarkovcraft.core.common.init.BaseTradeResources;
import tnt.tarkovcraft.core.common.init.filter.itemstack.ItemStackFilter;
import tnt.tarkovcraft.core.common.init.filter.itemstack.ItemStackFilterType;
import tnt.tarkovcraft.core.util.PlayerHelper;

import java.util.Collections;
import java.util.List;

public class ItemResource implements TradeResource {

    public static final MapCodec<ItemResource> CODEC = RecordCodecBuilder.<ItemResource>mapCodec(instance -> instance.group(
            ItemStackFilterType.ID_CODEC.listOf().optionalFieldOf("filters", Collections.emptyList()).forGetter(resource -> resource.filters),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(resource -> resource.count),
            ItemStack.CODEC.optionalFieldOf("result", ItemStack.EMPTY).forGetter(resource -> resource.resource)
    ).apply(instance, ItemResource::new)).validate(ItemResource::validate);

    private final List<ItemStackFilter> filters;
    private final int count;
    private final ItemStack resource;

    public ItemResource(List<ItemStackFilter> filters, int count, ItemStack resource) {
        this.filters = filters;
        this.count = count;
        this.resource = resource;
    }

    private static DataResult<ItemResource> validate(ItemResource in) {
        if (!in.resource.isEmpty())
            return DataResult.success(in);
        if (in.filters.isEmpty())
            return DataResult.error(() -> "Item resource must either specify input filter to qualify as INPUT or result to qualify as a OUTPUT");
        return DataResult.success(in);
    }

    @Override
    public boolean validateAvailability(TradeContext context) {
        ItemStack itemStack = this.findItem(context);
        return !itemStack.isEmpty();
    }

    @Override
    public void consume(TradeContext context) {
        int remainingAmount = this.count;
        while (remainingAmount > 0) {
            ItemStack itemStack = this.findItem(context);
            int count = itemStack.getCount();
            if (itemStack.isEmpty()) {
                TarkovCraftCore.LOGGER.error("Attempted to consume an empty item resource for {} while requiring remaining amount of {} from total {}", context, remainingAmount, this.count);
                break;
            }
            int toRemove = Math.min(remainingAmount, count);
            remainingAmount -= toRemove;
            itemStack.shrink(toRemove);
            if (remainingAmount <= 0)
                break;
        }
    }

    @Override
    public void produce(TradeContext context) {
        ItemStack result = this.resource.copy();
        PlayerHelper.giveItem(result, context.player());
    }

    @Override
    public TradeResourceType<?> getType() {
        return BaseTradeResources.ITEM_RESOURCE.get();
    }

    private ItemStack findItem(TradeContext context) {
        Container inventory = context.tradeInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack.isEmpty())
                continue;
            if (this.filters.stream().allMatch(filter -> filter.test(itemStack)))
                return itemStack;
        }
        return ItemStack.EMPTY;
    }
}
