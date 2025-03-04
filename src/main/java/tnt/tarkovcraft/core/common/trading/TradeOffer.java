package tnt.tarkovcraft.core.common.trading;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public final class TradeOffer {

    private List<TradeResource> inputs;
    private TradeResource output;
    private List<TradeCondition> conditions;
    private Object2IntMap<UUID> boughtAmount;
    private Integer supplyAmount;
    private Integer personalPurchaseLimit;

    public boolean isTradeOfferVisible(TradeContext context) {
        return this.conditions.stream().allMatch(condition -> condition.canShowOffer(context));
    }

    public boolean isTradeOfferCompletable(TradeContext context) {
        Player player = context.player();
        // Global purchase limit
        if (this.supplyAmount != null && this.getTotalBoughtAmount() >= this.supplyAmount) {
            return false;
        }
        // Personal purchase limit
        if (this.personalPurchaseLimit != null && this.getBoughtAmount(player.getUUID()) >= this.personalPurchaseLimit) {
            return false;
        }
        // All conditions fullfilled
        if (!this.conditions.stream().allMatch(condition -> condition.canCompleteTrade(context))) {
            return false;
        }
        // Input resource availability check
        return this.inputs.stream().allMatch(resource -> resource.validateAvailability(context));
    }

    public void onTradeCompleted(TradeContext context) {
        this.inputs.forEach(resource -> resource.consume(context));
        this.output.produce(context);
        if (this.supplyAmount != null || this.personalPurchaseLimit != null) {
            UUID tradingPlayerId = context.player().getUUID();
            int amount = this.getBoughtAmount(tradingPlayerId);
            this.setPersonalPurchasedAmount(tradingPlayerId, amount + 1);
        }
    }

    public int getBoughtAmount(@Nullable UUID playerId) {
        if (playerId == null) {
            return this.boughtAmount.values().intStream().sum();
        }
        return this.boughtAmount.getOrDefault(playerId, 0);
    }

    public int getTotalBoughtAmount() {
        return this.getBoughtAmount(null);
    }

    public void restock() {
        this.boughtAmount.clear();
    }

    public void setPersonalPurchasedAmount(UUID playerId, int amount) {
        int limit = this.personalPurchaseLimit != null ? this.personalPurchaseLimit : 0;
        this.boughtAmount.put(playerId, Mth.clamp(amount, 0, limit));
    }
}
