package tnt.tarkovcraft.core.common.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tnt.tarkovcraft.core.common.init.CoreItemDataComponents;

public class CurrencyItem extends Item {

    public CurrencyItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        Currency currency = Currency.getFromItemStack(stack);
        if (currency == null)
            return false;
        if (action == ClickAction.PRIMARY) {
            Currency otherCurrency = Currency.getFromItemStack(other);
            if (otherCurrency == null)
                return false;
            if (!currency.canMerge(otherCurrency))
                return false;
            Pair<Currency, Currency> pair = currency.merge(otherCurrency);
            Currency primary = pair.getFirst();
            Currency secondary = pair.getSecond();
            if (secondary == null) {
                other.setCount(0);
            } else {
                secondary.setToItemStack(other);
            }
            stack.set(CoreItemDataComponents.CURRENCY, primary);
            this.broadcastChanges(player);
        } else if (action == ClickAction.SECONDARY && other.isEmpty() && currency.amount() > 1) {
            ItemStack copy = stack.copy();
            Pair<Currency, Currency> splitCurrency = currency.split();
            splitCurrency.getFirst().setToItemStack(stack);
            splitCurrency.getSecond().setToItemStack(copy);
            access.set(copy);
            this.broadcastChanges(player);
        }

        return true;
    }

    private void broadcastChanges(Player player) {
        AbstractContainerMenu abstractcontainermenu = player.containerMenu;
        if (abstractcontainermenu != null) {
            abstractcontainermenu.slotsChanged(player.getInventory());
        }
    }
}
